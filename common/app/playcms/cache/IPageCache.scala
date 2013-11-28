package playcms.cache

import playcms.models.Page
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration.Duration
import play.api.cache.Cache

trait IPageCache extends ICache[Page]

class PageCache(implicit val ec: ExecutionContext) extends IPageCache {
  private def keyFrom(page: Page) = s"page:${page.id.get}"

  def get(key: String): Future[Option[Page]] =
    Future(Cache.getAs[Page](s"page:$key"))

  def set(page: Page, timeout: Duration): Unit =
    Cache.set(keyFrom(page), page, timeout.isFinite() match {
      case true => timeout.toSeconds.toInt
      case false => 0
    })

  def remove(page: Page): Unit = Cache.remove(keyFrom(page))

  def removeByKey(key: String): Unit = Cache.remove(s"page:$key")
}
