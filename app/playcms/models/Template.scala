package playcms.models

import reactivemongo.bson._
import play.api.libs.json.Json

case class Template(
  id: Option[String],
  name: String,
  templateText: String,
  isDeleted: Boolean,
  contentType: Option[String] = Some("text/html")
) extends Model with SoftDelete {
  def withId: Template = copy(id = Some(this.id getOrElse BSONObjectID.generate.stringify))
}

object Template {
  implicit object TemplateBSONHandler
    extends BSONDocumentReader[Template]
    with BSONDocumentWriter[Template]
    with BSONHandler[BSONDocument, Template] {

    def write(template: Template): BSONDocument =
      BSONDocument(
        "id" -> template.id.map(id => new BSONObjectID(id)).get,
        "name" -> template.name,
        "templateText" -> template.templateText,
        "isDeleted" -> template.isDeleted,
        "contentType" -> template.contentType)

    def read(bson: BSONDocument): Template =
      Template(
        bson.getAs[BSONObjectID]("_id").map(_.stringify),
        bson.getAs[String]("name").get,
        bson.getAs[String]("templateText").get,
        bson.getAs[Boolean]("isDeleted").get,
        bson.getAs[String]("contentType"))
  }

  implicit val templateFormats = Json.format[Template]
}