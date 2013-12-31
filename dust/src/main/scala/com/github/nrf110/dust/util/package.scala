package com.github.nrf110.dust

import java.net.URI
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import scala.concurrent.{ExecutionContext, Promise, Future}

package object util {
  private val HCHARS = "[&<>\"]"
  private val AMP = "&"
  private val LT = "<"
  private val GT = ">"
  private val QUOT = "\""

  implicit class PimpedString(s: String) {
    def replaceAll(args: (String, String)*) =
      args.foldLeft(s) { case (accum, (pattern, replace)) => accum.replaceAll(pattern, replace) }

    def escapeHtml =
      if (s.matches(HCHARS))
        s.replaceAll(
          AMP -> "&amp;",
          LT -> "&lt;",
          GT -> "&gt;",
          QUOT -> "&quot;")
      else s

    //TODO: implement this
    def escapeJs: String = ???
    //TODO: implement this
    def encodeUriComponent = ???

    def encodeUri = URI.create(s).toASCIIString
  }

  implicit class PimpedTry[A](t: Try[A]) {
    def fold[B](onSuccess: A => B, onFailure: Throwable => B): B =
      t match {
        case Success(a) => onSuccess(a)
        case Failure(NonFatal(t: Throwable)) => onFailure(t)
        case Failure(t: Throwable) => throw t
      }
  }

  implicit class PimpedFuture[A](f: Future[A]) {
    def fold[B](onSuccess: A => B, onFailure: Throwable => B)
               (implicit executionContext: ExecutionContext): Future[B] = {
      val promise = Promise[B]()
      f.onComplete {
        case Success(a) => promise.success(onSuccess(a))
        case Failure(NonFatal(t: Throwable)) => promise.success(onFailure(t))
        case Failure(t: Throwable) => promise.failure(t)
      }
      promise.future
    }
  }

  def withPromise[A](fn: Promise[A] => Unit): Future[A] = {
    val promise = Promise[A]()
    fn(promise)
    promise.future
  }
}
