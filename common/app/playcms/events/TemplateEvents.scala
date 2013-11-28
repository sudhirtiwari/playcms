package playcms.events

import play.api.libs.json._
import playcms.models.Template

object TemplateAddedEvent extends CmsEventCompanion[TemplateAddedEvent] {
  val destination = "template:added"
  val reads = Template.templateFormat.map(TemplateAddedEvent(_))
}
case class TemplateAddedEvent(template: Template) extends CmsEvent {
  type Body = Template
  def companion = TemplateAddedEvent
  def body = template
}

object TemplateUpdatedEvent extends CmsEventCompanion[TemplateUpdatedEvent] {
  val destination = "template:updated"
  val reads = Template.templateFormat.map(TemplateUpdatedEvent(_))
}
case class TemplateUpdatedEvent(template: Template) extends CmsEvent {
  type Body = Template
  def companion = TemplateUpdatedEvent
  def body = template
}

object TemplateDeletedEvent extends CmsEventCompanion[TemplateDeletedEvent] {
  val destination = "template:deleted"
  val reads = __.read[Template.ID].map(TemplateDeletedEvent(_))
}
case class TemplateDeletedEvent(id: Template.ID) extends CmsEvent {
  type Body = Template.ID
  def companion = TemplateDeletedEvent
  def body = id
}