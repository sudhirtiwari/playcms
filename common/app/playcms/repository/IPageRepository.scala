package playcms.repository

import play.api.libs.json.{Writes, Format, Json}
import playcms.models.{Site, Page}
import reactivemongo.api.DefaultDB
import scala.concurrent.{ExecutionContext, Future}

trait IPageRepository { this: MongoSoftDeleteRepository[Page, Page.ID] =>
  def findById(id: Page.ID): Future[Option[Page]]
  def findBySite(siteId: Site.ID): Future[Seq[Page]]
  def findAll: Future[List[Page]]
  def findChildren(siteId: Site.ID, parentId: Option[Page.ID]): Future[Seq[Page]]
  def saveAndReload(page: Page): Future[Page]
  def delete(id: Page.ID): Future[Unit]
  def softDelete(id: Page.ID): Future[Unit]
}

class MongoPageRepository(db: DefaultDB)(implicit ec: ExecutionContext, format: Format[Page], idWrites: Writes[Page.ID])
  extends MongoSoftDeleteRepository[Page, Page.ID](db)(ec, format, idWrites) with IPageRepository {

  val collectionName = "cms_pages"

  def findBySite(siteId: Site.ID) = find(Json.obj("siteId" -> siteId))
  def findChildren(siteId: Site.ID, id: Option[Page.ID]) = id match {
    case Some(value) => find(Json.obj("siteId" -> siteId ,"parentId" -> value))
    case None        => find(Json.obj("siteId" -> siteId, "parentId" -> Json.obj("$exists" -> false)))
  }
}
