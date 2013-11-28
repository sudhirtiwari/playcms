package playcms.cache

import play.api.cache.Cache
import playcms.models.RouteEntry
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

trait IRouteEntryCache extends ICache[RouteEntry]

class RouteEntryCache(implicit val ec: ExecutionContext) extends IRouteEntryCache {
  private def keyFrom(routeEntry: RouteEntry) = s"route:${routeEntry.fqdn}:${routeEntry.path}"

  def get(key: String): Future[Option[RouteEntry]] =
    Future(Cache.getAs[RouteEntry](s"route:$key"))

  def set(routeEntry: RouteEntry, timeout: Duration) =
    Cache.set(keyFrom(routeEntry), routeEntry, timeout.isFinite() match {
      case true   => timeout.toSeconds.toInt
      case false  => 0
    })

  def remove(routeEntry: RouteEntry): Unit = Cache.remove(keyFrom(routeEntry))
  def removeByKey(key: String): Unit = Cache.remove(s"route:$key")
}
