package playcms.models

import reactivemongo.bson._

case class Site(
  id: Option[String],
  title: String,
  description: Option[String],
  domain: String
) extends Domain {
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
        "domain" -> site.domain,
        "description" -> site.description)

    def read(bson: BSONDocument): Site =
      Site(
        bson.getAs[BSONObjectID]("_id").map(_.stringify),
        bson.getAs[String]("title").get,
        bson.getAs[String]("description"),
        bson.getAs[String]("domain").get)
  }
}
