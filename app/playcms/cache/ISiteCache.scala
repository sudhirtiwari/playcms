package playcms.cache

import playcms.models.Site
import play.api.cache.Cache
import scala.concurrent.duration.Duration
import scala.concurrent.{Future, ExecutionContext}

trait ISiteCache {
  def get(domain: String)(implicit ec: ExecutionContext): Future[Option[Site]]
  def set(site: Site, timeout: Duration = Duration.Inf)
}

class SiteCache extends ISiteCache {
  import play.api.Play.current
  def get(domain: String)(implicit ec: ExecutionContext) = Future(Cache.getAs[Site](s"site:$domain"))
  def set(site: Site, timeout: Duration) = {
    val expires = timeout.isFinite() match {
      case true => timeout.toSeconds.toInt
      case false => 0
    }
    Cache.set(s"site:${site.domain}", site, expires)
  }
}
