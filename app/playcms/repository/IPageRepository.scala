package playcms.repository

import play.api.libs.json.{Format, Json}
import playcms.models.Page
import reactivemongo.api.DefaultDB
import scala.concurrent.{ExecutionContext, Future}

trait IPageRepository { this: MongoSoftDeleteRepository[Page] =>
  def findById(id: String): Future[Option[Page]]
  def findBySite(siteId: String): Future[Seq[Page]]
  def findAll: Future[List[Page]]
  def findChildren(siteId: String, parentId: Option[String]): Future[Seq[Page]]
  def saveAndReload(page: Page): Future[Page]
  def delete(id: String): Future[Unit]
  def softDelete(id: String): Future[Unit]
}

class MongoPageRepository(db: DefaultDB)(implicit ec: ExecutionContext, format: Format[Page])
  extends MongoSoftDeleteRepository[Page](db) with IPageRepository {

  val collectionName = "cms_pages"

  def findBySite(siteId: String) = find(Json.obj("siteId" -> siteId))
  def findChildren(siteId: String, id: Option[String]) = id match {
    case Some(value) => find(Json.obj("siteId" -> siteId ,"parentId" -> value))
    case None        => find(Json.obj("siteId" -> siteId, "parentId" -> Json.obj("$exists" -> false)))
  }
}
