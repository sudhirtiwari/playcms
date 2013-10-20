package playcms.cache

import playcms.models.Template
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration
import play.api.cache.Cache

trait ITemplateCache extends ICache[Template]

class TemplateCache(implicit val ec: ExecutionContext) extends ITemplateCache {
  private def keyFrom(template: Template) = template.name

  def get(key: String) =
    Future(Cache.getAs[Template](s"template:$key"))

  def set(template: Template, timeout: Duration = Duration.Inf) =
    Cache.set(keyFrom(template), template, timeout.isFinite() match {
      case true   => timeout.toSeconds.toInt
      case false  => 0
    })

  def remove(template: Template) = Cache.remove(keyFrom(template))
  def removeByKey(key: String) = Cache.remove(s"template:$key")
}