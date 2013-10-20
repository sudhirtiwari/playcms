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
  implicit object SiteBSONHandler
    extends BSONDocumentReader[Site]
    with BSONDocumentWriter[Site]
    with BSONHandler[BSONDocument, Site] {

    def write(site: Site): BSONDocument =
      BSONDocument(
        "_id" -> site.id.map(id => new BSONObjectID(id)).get,
        "title" -> site.title,
        "description" -> site.description,
        "name" -> site.name,
        "domain" -> site.domain,
        "isDeleted" -> site.isDeleted)

    def read(bson: BSONDocument): Site =
      Site(
        bson.getAs[BSONObjectID]("_id").map(_.stringify),
        bson.getAs[String]("parentId"),
        bson.getAs[String]("title").get,
        bson.getAs[String]("description"),
        bson.getAs[String]("name").get,
        bson.getAs[String]("domain"),
        bson.getAs[Boolean]("isDeleted").get)
  }

  implicit val siteFormat = Json.format[Site]
}
