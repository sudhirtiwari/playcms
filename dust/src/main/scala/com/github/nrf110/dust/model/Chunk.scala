package com.github.nrf110.dust
package model

import util._
import akka.actor.ActorDSL._
import akka.actor.ActorRefFactory
import scala.annotation.tailrec

class Chunk(
  root: StreamLike,
  next: Option[Chunk] = None,
  taps: Option[Tap] = None,
  error: Option[Throwable] = None,
  flushable: Boolean = false)(implicit actorRefFactory: ActorRefFactory) {

  import Chunk._
  private lazy val chunkActor = actor(new Act {
    become(handle(
      ChunkData(next, taps, flushable, error)
    ))

    def handle(data: ChunkData): Receive = {
      case GetData => sender ! data
      case AddTap(tap) => withAck(addTap(tap, data))
      case RemoveTap => withAck(untap(data))
      case Write(content) => withAck(write(content, data))
      case End(content) => withAck(end(content, data))
      case SetError(err) => withAck(data.copy(error = Some(err)))
      case Map(fn) => withAck(map(data)(fn))
    }

    def addTap(tap: String => String, data: ChunkData) = data.copy(taps = Option(Tap(tap, data.taps)))

    def untap(data: ChunkData) = data.copy(taps = data.taps flatMap (_.tail))

    def write(content: String, data: ChunkData) =
      data.copy(buffer = data.buffer + (data.taps match {
        case Some(tap) => tap.go(content)
        case None => content
      }))

    def end(content: Option[String], data: ChunkData) = {
      val newData = content map (write(_, data)) getOrElse data
      root.flush
      newData.copy(flushable = true)
    }

    def map(data: ChunkData)(fn: Chunk => Unit) = {
      val cursor = Chunk(
        root = root,
        next = data.next,
        taps = data.taps)

      val branch = Chunk(
        root = root,
        next = Option(cursor),
        taps = data.taps)

      fn(branch)
      sender ! cursor

      data.copy(
        next = Option(branch),
        flushable = true)
    }

    def withAck(fn: => ChunkData) = {
      become(handle(fn))
      sender ! Ack
    }
  })

  def end(content: Option[String]) = waitForAck(End(content))
  def write(content: String) = waitForAck(Write(content))
  def tap(tap: String => String) = waitForAck(AddTap(tap))
  def untap = waitForAck(RemoveTap)
  def setError(error: Throwable) = waitForAck(SetError(error))
  def map(fn: Chunk => Unit) = withPromise[Chunk] { promise =>
    chunkActor.tell(Map(fn), HandleOne {
      case chunk: Chunk => promise.success(chunk)
    })
  }
  def getData = withPromise[ChunkData] { promise =>
    chunkActor.tell(GetData, HandleOne {
      case data: ChunkData => promise.success(data)
    })
  }

  private def waitForAck(msg: Any) = withPromise[Chunk] { promise =>
    chunkActor.tell(msg, HandleOne {
      case Ack => promise.success(this)
    })
  }
}

object Chunk {
  def apply(
    root: StreamLike,
    next: Option[Chunk] = None,
    taps: Option[Tap] = None,
    error: Option[Throwable] = None,
    flushable: Boolean = false)(implicit actorRefFactory: ActorRefFactory): Chunk =
    new Chunk(root, next, taps, error, flushable)

  case object GetData
  case object Ack
  case class AddTap(tap: String => String)
  case object RemoveTap
  case class Write(content: String)
  case class End(content: Option[String] = None)
  case class SetError(error: Throwable)
  case class Map(fn: Chunk => Unit)
  case class ChunkData(
    next: Option[Chunk],
    taps: Option[Tap],
    flushable: Boolean,
    error: Option[Throwable] = None,
    buffer: String = "")
}

private[model] case class Tap(
  head: String => String,
  tail: Option[Tap] = None) {
  def push(tap: (String) => String) = Tap(tap, Option(this))
  def go(data: String): String = {
    @tailrec
    def step(tap: Tap, accum: String): String =
      tap.tail match {
        case Some(rest) => step(rest, tap.head(accum))
        case None => tap.head(accum)
      }

    step(this, data)
  }
}
