package com.github.nrf110
package model

import util._
import scala.concurrent.Promise
import scala.util.Try

trait StreamLike {
  def head: Chunk
  def flush: StreamLike
}

case class Stub(
  head: ChunkLike = Chunk(this),
  promise: Promise[String]) extends StreamLike {
  def flush = {
    def step(chunk: ChunkLike, accum: String = ""): (ChunkLike, String) =
      chunk match {
        case Chunk(_, next, data, true) => next map (step(_, accum + data)) getOrElse (chunk, accum)
        case ErrorChunk(t) => throw t
        case Chunk(_, _, _, false) => (chunk, accum)
      }

    Try(step(head)) fold (
      onSuccess = {
        case (chunk, data) =>
          promise.success(data)
          copy(head = chunk)
      },
      onFailure = { t =>
        promise.failure(t)
        copy(head = ErrorChunk(t))
      }
    )
  }
}

case class Stream(
  head: ChunkLike = Chunk(this),
  events: Map[StreamEvent, StreamEvent => Unit] = Map.empty[StreamEvent, StreamEvent => Unit]) extends StreamLike {
  def on(event: StreamEvent)(handler: StreamEvent => Unit) = copy(events = events.updated(event, handler))
  def flush = {
    def step(chunk: ChunkLike): ChunkLike =
      chunk match {
        case Chunk(_, next, data, true) =>
          emit(Data(data))
          next map (step(_)) getOrElse(chunk)
        case ErrorChunk(t) =>
          emit(Error(t))
          chunk
        case Chunk(_, _, _, false) =>
          emit(End)
          chunk
      }

    copy(head = step(head))
  }
  private def emit(event: StreamEvent) = events.get(event) foreach (_.apply(event))
}

sealed trait StreamEvent
case class Data(data: String) extends StreamEvent
case object End extends StreamEvent
case class Error(t: Throwable) extends StreamEvent

sealed trait ChunkLike {
  def flushable: Boolean
  def hasError: Boolean
  def next: Option[ChunkLike]
}
case class Chunk(
  root: StreamLike,
  next: Option[ChunkLike] = None,
  data: String = "",
  flushable: Boolean = false
) extends ChunkLike {
  def hasError = false
}

case class ErrorChunk(t: Throwable) extends ChunkLike {
  def flushable = false
  def hasError = true
  def next = None
}
