package playcms.models

import reactivemongo.bson._
import play.api.libs.json.Json

case class Template(
  id: Option[Template.ID],
  name: String,
  templateText: String,
  isDeleted: Boolean,
  contentType: Option[String] = Some("text/html")
) extends Model[Template.ID] with SoftDelete {
  def withId: Template = copy(id = Some(this.id getOrElse BSONObjectID.generate.stringify))
}

object Template {
  type ID = String
  val templateFormat = Json.format[Template]
}