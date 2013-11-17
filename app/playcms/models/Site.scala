package playcms.models

import reactivemongo.bson._
import play.api.libs.json.Json

case class Site(
  id: Option[String],
  parentId: Option[String],
  title: String,
  description: Option[String],
  name: String,
  domain: Option[String],
  isDeleted: Boolean
) extends Model with SoftDelete {
  def withId: Site = copy(id = Some(this.id getOrElse BSONObjectID.generate.stringify))
}

object Site {
  implicit val siteFormat = Json.format[Site]
}
