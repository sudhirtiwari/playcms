package playcms.cache

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration.Duration
import playcms.models.Site
import scala.util.Success

trait ICache[T] {
  implicit def app = play.api.Play.current
  implicit def ec: ExecutionContext

  def get(key: String): Future[Option[T]]

  /**
   * Get the value for the key if it exists.  If not, use the factory to retrieve it, and update the cache
   * if the factory method produces a result
   * @param key
   * @param factory
   * @return
   */
  def getOrElse(key: String)(factory: => Future[Option[T]]): Future[Option[T]] = {
    get(key) flatMap {
      case success @ Some(value) => Future.successful(success)
      case _ => factory andThen {
        case Success(Some(value)) => set(value)
        case _ => Future.successful(None)
      }
    }
  }
  def set(item: T, timeout: Duration = Duration.Inf)

  /**
   * Only set the value for the key if the key already exists.
   * This is to prevent out-of-order updates to items which should have been deleted
   * @param key
   * @param item
   */
  def setIfExists(key: String, item: T) =
    get(key) foreach (_ => set(item))

  def remove(item: T)

  def removeByKey(key: String)
}
