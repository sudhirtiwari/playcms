package com.github.nrf110
package model

import util._
import scala.annotation.tailrec
import scala.concurrent.Promise
import scala.util.Try

trait StreamLike {
  def head: Chunk
  def withHead(head: Chunk): StreamLike
  def flush: StreamLike
}

class Stub(
  promise: Promise[String],
  var head: Chunk = Chunk(this)) extends StreamLike {
  def withHead(head: Chunk) = {
    this.head = head
    this
  }

  def flush = {
    def step(chunk: Chunk, accum: String = ""): (Chunk, String) =
      chunk match {
        case Chunk(_, next, data, _, _, true) => next map (step(_, accum + data)) getOrElse (chunk, accum)
        case Chunk(_, _, _, _, Some(t), _) => throw t
        case Chunk(_, _, _, _, _, false) => (chunk, accum)
      }

    Try(step(head)) fold (
      onSuccess = {
        case (chunk, data) =>
          promise.success(data)
          this.head = chunk
          this
      },
      onFailure = { t =>
        promise.failure(t)
        this.head = head.withError(t)
        this
      }
    )
  }
}

object Stub {
  def apply(promise: Promise[String]): Stub = new Stub(promise)
  def apply(promise: Promise[String], head: Chunk): Stub = new Stub(promise, head)
  def unapply(stub: Stub): Option[Chunk] = Option(stub.head)
}

class Stream(
  var head: Chunk = Chunk(this),
  var events: Map[Stream.Event, Stream.Event => Unit] = Map.empty[Stream.Event, Stream.Event => Unit]) extends StreamLike {

  def withHead(head: Chunk) = {
    this.head = head
    this
  }

  def on(event: Stream.Event)(handler: Stream.Event => Unit) = {
    this.events = events.updated(event, handler)
    this
  }

  def flush = {
    def step(chunk: Chunk): Chunk =
      chunk match {
        case Chunk(_, next, data, _, _, true) =>
          emit(Stream.Data(data))
          next map step getOrElse chunk
        case Chunk(_, _, _, _, Some(t), _) =>
          emit(Stream.Error(t))
          chunk
        case Chunk(_, _, _, _, _, false) =>
          emit(Stream.End)
          chunk
      }

    this.head = step(head)
    this
  }

  private def emit(event: Stream.Event) = events.get(event) foreach (_.apply(event))
}

object Stream {
  sealed trait Event
  case class Data(data: String) extends Event
  case object End extends Event
  case class Error(t: Throwable) extends Event

  def apply(): Stream = new Stream()
  def apply(head: Chunk) = new Stream(head = head)
  def apply(events: Map[Event, Event => Unit]) = new Stream(events = events)
  def apply(head: Chunk, events: Map[Event, Event => Unit]) = new Stream(head, events)
  def unapply(stream: Stream): Option[Chunk] = Option(stream.head)
}

class Chunk(
  val root: StreamLike,
  var next: Option[Chunk] = None,
  var data: String = "",
  var taps: Option[Tap] = None,
  var error: Option[Throwable] = None,
  var flushable: Boolean = false) {

  def withError(t: Throwable) = {
    error = Option(t)
    this
  }
  def tap(fn: String => String) = {
    taps = Option(Tap(fn, this.taps))
    this
  }
  def untap = {
    taps = taps flatMap (_.tail)
    this
  }

  def write(content: String) = {
    data = taps match {
      case Some(tap) => data + tap.go(content)
      case None => data + content
    }
    this
  }

  def end(content: Option[String] = None) = {
    content foreach write
    flushable = true
    root.flush
    this
  }

  def map(fn: Chunk => Unit) = {
    val cursor = Chunk(
      root = this.root,
      next = this.next,
      taps = this.taps)

    val branch = Chunk(
      root = this.root,
      next = Option(cursor),
      taps = this.taps)

    next = Option(branch)
    flushable = true
    fn(branch)
    cursor
  }
}

object Chunk {
  def apply(root: StreamLike, next: Option[Chunk] = None, taps: Option[Tap] = None): Chunk =
    new Chunk(
      root = root,
      next = next,
      taps = taps)

  def unapply(chunk: Chunk): Option[(StreamLike, Option[Chunk], String, Option[Tap], Option[Throwable], Boolean)] =
    Option((
      chunk.root,
      chunk.next,
      chunk.data,
      chunk.taps,
      chunk.error,
      chunk.flushable))
}

case class Tap(
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
