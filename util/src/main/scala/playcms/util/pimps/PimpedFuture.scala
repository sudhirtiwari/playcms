package playcms.util.pimps

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Try, Failure, Success}

class PimpedFuture[A](underlying: Future[A]) {
  def fold[B](onSuccess: (A) => B, onFailure: (Throwable) => B)
             (implicit executionContext: ExecutionContext): Future[B] = {
    val p = Promise[B]
    underlying onComplete {
      case Success(a) => p.complete(Try(onSuccess(a)))
      case Failure(t) => p.complete(Try(onFailure(t)))
    }
    p.future
  }
}
