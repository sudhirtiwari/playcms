package playcms.cache

import playcms.models.Site
import scala.concurrent.duration.Duration
import scala.concurrent.{Future, ExecutionContext}
import play.api.cache.Cache
import scala.util.Success

trait ISiteCache extends ICache[Site]

class SiteCache(implicit val ec: ExecutionContext) extends ISiteCache {
  private def keyFrom(site: Site) = s"site:${site.id.get}"

  def get(key: String) =
    Future(Cache.getAs[Site](s"site:$key"))

  def set(site: Site, timeout: Duration) =
    Cache.set(keyFrom(site), site, timeout.isFinite() match {
      case true   => timeout.toSeconds.toInt
      case false  => 0
    })

  def remove(site: Site) = Cache.remove(keyFrom(site))
  def removeByKey(key: String) = Cache.remove(s"site:$key")
}
