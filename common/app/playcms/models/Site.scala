package playcms.models

import reactivemongo.bson._
import play.api.libs.json.Json

case class Site(
  id: Option[Site.ID],
  parentId: Option[Site.ID],
  title: String,
  description: Option[String],
  name: String,
  domain: Option[String],
  isDeleted: Boolean
) extends Model[Site.ID] with SoftDelete {
  def withId: Site = copy(id = Some(this.id getOrElse BSONObjectID.generate.stringify))
}

object Site {
  type ID = String
  val siteFormat = Json.format[Site]
}
