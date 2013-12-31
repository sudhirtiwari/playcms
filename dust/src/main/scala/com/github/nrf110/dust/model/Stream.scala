package com.github.nrf110.dust
package model

import util._
import akka.actor.ActorDSL._
import akka.actor.ActorRefFactory
import scala.concurrent.Future
import com.github.nrf110.dust.model.Chunk.ChunkData

class Stream(implicit actorRefFactory: ActorRefFactory) extends StreamLike { me =>
  import Stream._

  type HandlerRegistry = Map[Event, Event => Unit]

  private val streamActor = actor(new Act {
    import context.dispatcher
    become(handle(Chunk(me), Map.empty[Event, Event => Unit]))

    def handle(head: Chunk, handlers: HandlerRegistry): Receive = {
      case GetHead => sender ! head
      case SetHead(chunk) =>
        become(handle(chunk, handlers))
        sender ! Ack
      case AddEventHandler(event, handler) =>
        become(handle(head, handlers.updated(event, handler)))
        sender ! Ack
      case Flush => flush(head, handlers)
    }

    def failed(head: Chunk): Receive = {
      case GetHead => sender ! head
    }

    def flush(head: Chunk, handlers: HandlerRegistry) = {
      val origin = sender

      def emit(event: Event) = handlers.get(event) foreach (_.apply(event))
      def step(chunk: Chunk): Future[Chunk] =
        chunk.getData flatMap {
          case ChunkData(next, _, true, _, buffer) =>
            emit(Data(buffer))
            next map step getOrElse Future.successful(chunk)
          case ChunkData(_, _, _, Some(t), _) =>
            emit(Error(t))
            Future.successful(chunk)
          case ChunkData(_, _, false, _, _) =>
            emit(Stream.End)
            Future.successful(chunk)
        }

      step(head) fold (
        onSuccess = {
          case `head` => origin ! Ack
          case chunk: Chunk =>
            become(handle(chunk, handlers))
            origin ! Ack
        },
        onFailure = { t =>
          head.setError(t) onComplete { _ =>
            become(failed(head))
            origin ! Ack
          }
        })
    }
  })

  def on(event: Stream.Event)(handler: Stream.Event => Unit) = waitForAck(AddEventHandler(event, handler))
  def withHead(head: Chunk) = waitForAck(SetHead(head))
  def flush = waitForAck(Flush)
  def head = withPromise[Chunk] { promise =>
    streamActor.tell(GetHead, HandleOne {
      case chunk: Chunk => promise.success(chunk)
    })
  }
  private def waitForAck(msg: Any) = withPromise[StreamLike] { promise =>
    streamActor.tell(msg, HandleOne {
      case Ack => promise.success(this)
    })
  }
}

object Stream {
  sealed trait Event
  case class Data(data: String) extends Event
  case object End extends Event
  case class Error(t: Throwable) extends Event

  case object Ack
  case class SetHead(chunk: Chunk)
  case object GetHead
  case object Flush
  case class AddEventHandler(event: Event, listener: Event => Unit)
}
