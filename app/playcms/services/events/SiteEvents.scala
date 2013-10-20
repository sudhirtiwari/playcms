package playcms.services.events

import play.api.libs.json._
import playcms.models.Site

object SiteAddedEvent extends CmsEventCompanion[SiteAddedEvent] {
  val destination = "site:added"
  val reads = Site.siteFormat.map(SiteAddedEvent(_))
}
case class SiteAddedEvent(site: Site) extends CmsEvent {
  type Body = Site
  def companion = SiteAddedEvent
  def body = site
}

object SiteUpdatedEvent extends CmsEventCompanion[SiteUpdatedEvent] {
  val destination = "site:updated"
  val reads = Site.siteFormat.map(SiteUpdatedEvent(_))
}
case class SiteUpdatedEvent(site: Site) extends CmsEvent {
  type Body = Site
  def companion = SiteUpdatedEvent
  def body = site
}

object SiteDeletedEvent extends CmsEventCompanion[SiteDeletedEvent] {
  val destination = "site:deleted"
  val reads = __.read[String].map(SiteDeletedEvent(_))
}
case class SiteDeletedEvent(domain: String) extends CmsEvent {
  type Body = String
  def companion = SiteDeletedEvent
  def body = domain
}