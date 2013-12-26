package com.github.nrf110

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

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
  }

  implicit class PimpedTry[A](t: Try[A]) {
    def fold[B](onSuccess: A => B, onFailure: Throwable => B): B =
      t match {
        case Success(a: A) => onSuccess(a)
        case Failure(NonFatal(t)) => onFailure(t)
        case Failure(t) => throw t
      }
  }
}
