package playcms.repository

import playcms.models.Page
import reactivemongo.api.DefaultDB
import reactivemongo.bson.BSONDocument
import scala.concurrent.{ExecutionContext, Future}

trait IPageRepository { this: MongoSoftDeleteRepository[Page] =>
  def findById(id: String): Future[Option[Page]]
  def findBySite(siteId: String): Future[Seq[Page]]
  def findAll: Future[List[Page]]
  def findChildren(id: Option[String]): Future[Seq[Page]]
  def saveAndReload(page: Page): Future[Page]
  def delete(id: String): Future[Unit]
  def softDelete(id: String): Future[Unit]
}

class MongoPageRepository(db: DefaultDB)(implicit ec: ExecutionContext)
  extends MongoSoftDeleteRepository[Page](db) with IPageRepository {

  val collectionName = "cms_pages"
  implicit val bsonHandler = Page.PageBSONHandler

  def findBySite(siteId: String) = find(BSONDocument("siteId" -> siteId))
  def findChildren(id: Option[String]) = id match {
    case Some(value) => find(BSONDocument("parentId" -> value))
    case None        => find(BSONDocument("parentId" -> BSONDocument("$exists" -> false)))
  }
}
