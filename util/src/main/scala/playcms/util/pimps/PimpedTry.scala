package playcms.util.pimps

import scala.util.{Failure, Success, Try}

class PimpedTry[A](underlying: Try[A]) {
  def fold[B](onSuccess: (A) => B, onFailure: (Throwable) => B): B = underlying match {
    case Success(a) => onSuccess(a)
    case Failure(e) => onFailure(e)
  }
}
