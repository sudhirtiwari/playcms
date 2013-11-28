package playcms

import playcms.util.pimps._
import scala.concurrent.Future
import scala.util.Try

package object util {
  implicit def future2PimpedFuture[A](future: Future[A]) = new PimpedFuture[A](future)
  implicit def try2PimpedTry[A](action: Try[A]) = new PimpedTry[A](action)
}
