package com.github.nrf110.dust
package model

import util._
import Chunk.ChunkData
import akka.actor.ActorDSL._
import scala.concurrent.{Future, Promise}
import akka.actor.ActorRefFactory

class Stub(promise: Promise[String])(implicit actorRefFactory: ActorRefFactory) extends StreamLike { me =>

  import Stub._

  private val stubActor = actor(new Act {
    import context.dispatcher

    become(handle(Chunk(me)))

    def handle(head: Chunk, buffer: String = ""): Receive = {
      case Flush(p) => flush(head, buffer, p)
      case GetHead => sender ! head
      case SetHead(chunk) =>
        become(handle(chunk, buffer))
        sender ! Ack
    }

    def failed(head: Chunk): Receive = {
      case GetHead => sender ! head
    }

    def flush(head: Chunk, buffer: String, promise: Promise[String]) = {
      val origin = sender

      def step(chunk: Chunk, accum: String): Future[(Chunk, String)] =
        chunk.getData flatMap {
          case ChunkData(next, _, true, _, data) => next map (step(_, accum + data)) getOrElse Future.successful((chunk, accum))
          case ChunkData(_, _, _, Some(t), _) => Future.failed(t)
          case ChunkData(_, _, false, _, _) => Future.successful(chunk, accum)
        }

      step(head, buffer) fold (
        onSuccess = {
          case (`head`, `buffer`) => origin ! Ack
          case (chunk, data) =>
            promise.success(data)
            become(handle(chunk, data))
            origin ! Ack
        },
        onFailure = { t =>
          promise.failure(t)
          head.setError(t) onComplete { _ =>
            become(failed(head))
            origin ! Ack
          }
        })
    }
  })

  def withHead(head: Chunk) = waitForAck(SetHead(head))
  def flush = waitForAck(Flush(promise))
  def head = withPromise[Chunk] { promise =>
    stubActor.tell(GetHead, HandleOne {
      case chunk: Chunk => promise.success(chunk)
    })
  }
  private def waitForAck(msg: Any) = withPromise[StreamLike] { promise =>
    stubActor.tell(msg, HandleOne {
      case Ack => promise.success(this)
    })
  }
}

object Stub {
  case class Flush(promise: Promise[String])
  case class SetHead(chunk: Chunk)
  case object GetHead
  case object Ack
}
