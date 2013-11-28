package playcms.models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID

case class RouteEntry(
  id: Option[RouteEntry.ID],
  fqdn: String,
  path: String,
  pageId: Option[Page.ID],
  status: Int,
  redirectTo: Option[String]) extends Model[RouteEntry.ID] {
  def withId: RouteEntry = copy(id = Some(this.id getOrElse BSONObjectID.generate.stringify))
}

object RouteEntry {
  type ID = String
  val routeEntryFormat = Json.format[RouteEntry]
}
