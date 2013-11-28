package playcms.events

import playcms.models.Page
import play.api.libs.json._

object PageAddedEvent extends CmsEventCompanion[PageAddedEvent] {
  val destination = "page:added"
  val reads = Page.pageFormat.map(PageAddedEvent(_))
}
case class PageAddedEvent(page: Page) extends CmsEvent {
  type Body = Page
  def companion = PageAddedEvent
  def body = page
}

object PageUpdatedEvent extends CmsEventCompanion[PageUpdatedEvent] {
  val destination = "page:updated"
  val reads = Page.pageFormat.map(PageUpdatedEvent(_))
}
case class PageUpdatedEvent(page: Page) extends CmsEvent {
  type Body = Page
  def companion = PageUpdatedEvent
  def body = page
}

object PageDeletedEvent extends CmsEventCompanion[PageDeletedEvent] {
  val destination = "page:deleted"
  val reads = __.read[String].map(PageDeletedEvent(_))
}
case class PageDeletedEvent(id: Page.ID) extends CmsEvent {
  type Body = Page.ID
  def companion: CmsEventCompanion[_ <: CmsEvent] = PageDeletedEvent
  def body = id
}
