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
  implicit object PageBSONHandler
    extends BSONDocumentReader[Page]
    with BSONDocumentWriter[Page]
    with BSONHandler[BSONDocument, Page] {

    def write(page: Page): BSONDocument =
      BSONDocument(
        "_id" -> page.id.map(id => new BSONObjectID(id)).get,
        "siteId" -> page.siteId,
        "parentId" -> page.parentId,
        "templateId" -> page.templateId,
        "relativePath" -> page.relativePath,
        "fullPath" -> page.fullPath,
        "contentAreas" -> page.contentAreas.foldLeft(BSONDocument.empty)({
          case (doc, (name, content)) => doc ++ (name -> content)
        }),
        "isDeleted" -> page.isDeleted)

    def read(bson: BSONDocument): Page =
      Page(
        bson.getAs[BSONObjectID]("_id") map (_.stringify),
        bson.getAs[String]("siteId").get,
        bson.getAs[String]("parentId"),
        bson.getAs[String]("templateId").get,
        bson.getAs[String]("relativePath").get,
        bson.getAs[String]("fullPath"),
        bson.getAs[BSONDocument]("contentAreas").getOrElse(BSONDocument.empty).elements map ({
          case (name, content: BSONString) => name -> content.value
        }) toMap,
        bson.getAs[Boolean]("isDeleted").get)
  }

  implicit val contentAreaFormats = Format(Reads.map[String], Writes.map[String])

  implicit val pageFormats = (
    (__ \ 'id).formatNullable[String] ~
    (__ \ 'siteId).format[String] ~
    (__ \ 'parentId).formatNullable[String] ~
    (__ \ 'templateId).format[String] ~
    (__ \ 'relativePath).format[String] ~
    (__ \ 'fullPath).formatNullable[String] ~
    (__ \ 'contentAreas).format[Map[String, String]] ~
    (__ \ 'isDeleted).format[Boolean]
  )(Page(_, _, _, _, _, _, _, _), unlift(Page.unapply))

}