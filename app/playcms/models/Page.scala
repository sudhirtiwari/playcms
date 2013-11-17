package playcms.models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson._

case class Page(
  id: Option[String],
  siteId: String,
  parentId: Option[String],
  templateId: String,
  relativePath: String,
  fullPath: Option[String],
  contentAreas: Map[String, String],
  isDeleted: Boolean
) extends Model with SoftDelete {
  def withId: Page = copy(id = Some(this.id getOrElse BSONObjectID.generate.stringify))
}

object Page {
  implicit val contentAreaFormats = Format(Reads.map[String], Writes.map[String])

  implicit val pageFormat = (
    (__ \ '_id).formatNullable[String] ~
    (__ \ 'siteId).format[String] ~
    (__ \ 'parentId).formatNullable[String] ~
    (__ \ 'templateId).format[String] ~
    (__ \ 'relativePath).format[String] ~
    (__ \ 'fullPath).formatNullable[String] ~
    (__ \ 'contentAreas).format[Map[String, String]] ~
    (__ \ 'isDeleted).format[Boolean]
  )(Page.apply, unlift(Page.unapply))

}