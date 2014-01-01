package playcms.models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson._

case class Page(
  id: Option[Page.ID],
  siteId: Site.ID,
  parentId: Option[Page.ID],
  templateId: Template.ID,
  relativePath: String,
  fullPath: Option[String],
  contentAreas: Map[String, String],
  isDeleted: Boolean
) extends Model[Page.ID] with SoftDelete {
  def withId: Page = copy(id = Some(this.id getOrElse BSONObjectID.generate.stringify))
}

object Page {
  type ID = String
  implicit val contentAreaFormats = Format(Reads.map[String], Writes.map[String])
  val pageFormat = Json.format[Page]
}