package playcms.services.events

import play.api.libs.json._
import playcms.models.Template

object TemplateAddedEvent extends CmsEventCompanion[TemplateAddedEvent] {
  val destination = "template:added"
  val reads = Template.templateFormats.map(TemplateAddedEvent(_))
}
case class TemplateAddedEvent(template: Template) extends CmsEvent {
  type Body = Template
  def companion = TemplateAddedEvent
  def body = template
}

object TemplateUpdatedEvent extends CmsEventCompanion[TemplateUpdatedEvent] {
  val destination = "template:updated"
  val reads = Template.templateFormats.map(TemplateUpdatedEvent(_))
}
case class TemplateUpdatedEvent(template: Template) extends CmsEvent {
  type Body = Template
  def companion = TemplateUpdatedEvent
  def body = template
}

object TemplateDeletedEvent extends CmsEventCompanion[TemplateDeletedEvent] {
  val destination = "template:deleted"
  val reads = __.read[String].map(TemplateDeletedEvent(_))
}
case class TemplateDeletedEvent(id: String) extends CmsEvent {
  type Body = String
  def companion = TemplateDeletedEvent
  def body = id
}