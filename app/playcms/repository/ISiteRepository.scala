package playcms.repository

import concurrent.{Future, ExecutionContext}
import reactivemongo.api.DefaultDB
import playcms.models.Site
import reactivemongo.bson.{BSONObjectID, BSONDocument}

trait ISiteRepository { this: MongoRepository[Site] =>
  def findById(id: String)(implicit ec: ExecutionContext): Future[Option[Site]]
  def findAll(implicit ec: ExecutionContext): Future[List[Site]]
  def findByDomain(domain: String)(implicit ec: ExecutionContext): Future[Option[Site]]
  def saveAndReload(site: Site)(implicit ec: ExecutionContext): Future[Site]
  def delete(id: String)(implicit ec: ExecutionContext): Future[Unit]
  def isUnique(id: Option[String], domain: String)(implicit ec: ExecutionContext): Future[Boolean]

}

class MongoSiteRepository(db: DefaultDB)
  extends MongoRepository[Site](db)
  with ISiteRepository {

  val collectionName = "cms_sites"
  implicit val bsonHandler = Site.SiteBSONHandler

  def findByDomain(domain: String)(implicit ec: ExecutionContext) =
    findOne(BSONDocument("domain" -> domain))

  def isUnique(id: Option[String], domain: String)(implicit ec: ExecutionContext) = {
    findByDomain(domain) map (_.isEmpty)
  }
}