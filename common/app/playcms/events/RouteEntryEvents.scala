package playcms.events

import playcms.models.RouteEntry
import play.api.libs.json._

object RouteEntryAddedEvent extends CmsEventCompanion[RouteEntryAddedEvent] {
  val destination = "routeEntry:added"
  val reads = RouteEntry.routeEntryFormat.map(RouteEntryAddedEvent(_))
}
case class RouteEntryAddedEvent(routeEntry: RouteEntry) extends CmsEvent {
  type Body = RouteEntry
  def companion = RouteEntryAddedEvent
  def body = routeEntry
}

object RouteEntryUpdatedEvent extends CmsEventCompanion[RouteEntryUpdatedEvent] {
  val destination = "routeEntry:updated"
  val reads = RouteEntry.routeEntryFormat.map(RouteEntryUpdatedEvent(_))
}
case class RouteEntryUpdatedEvent(routeEntry: RouteEntry) extends CmsEvent {
  type Body = RouteEntry
  def companion = RouteEntryUpdatedEvent
  def body = routeEntry
}

object RouteEntryDeletedEvent extends CmsEventCompanion[RouteEntryDeletedEvent] {
  val destination = "routeEntry:deleted"
  val reads = __.read[RouteEntry.ID].map(RouteEntryDeletedEvent(_))
}
case class RouteEntryDeletedEvent(id: RouteEntry.ID) extends CmsEvent {
  type Body = RouteEntry.ID
  def companion = RouteEntryDeletedEvent
  def body = id
}