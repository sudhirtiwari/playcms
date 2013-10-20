package playcms.repository

import concurrent.{Future, ExecutionContext}
import reactivemongo.api.DefaultDB
import playcms.models.Site
import reactivemongo.bson.{BSONObjectID, BSONDocument}

trait ISiteRepository { this: MongoSoftDeleteRepository[Site] =>
  def findById(id: String): Future[Option[Site]]
  def findAll: Future[List[Site]]
  def findByDomain(domain: String): Future[Option[Site]]
  def findChildren(id: Option[String]): Future[Seq[Site]]
  def saveAndReload(site: Site): Future[Site]
  def delete(id: String): Future[Unit]
  def softDelete(id: String): Future[Unit]
}

class MongoSiteRepository(db: DefaultDB)(override implicit val ec: ExecutionContext)
  extends MongoSoftDeleteRepository[Site](db)
  with ISiteRepository {

  val collectionName = "cms_sites"
  implicit val bsonHandler = Site.SiteBSONHandler

  def findByDomain(domain: String) = findOne(BSONDocument("domain" -> domain))
  def findChildren(id: Option[String]) = id match {
    case Some(value) => find(BSONDocument("parentId" -> value))
    case None        => find(BSONDocument("parentId" -> BSONDocument("$exists" -> false)))
  }
}