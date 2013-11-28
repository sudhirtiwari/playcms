package playcms.repository

import play.api.libs.json.{Writes, Json, Format}
import playcms.models.Site
import reactivemongo.api.DefaultDB
import scala.concurrent.{Future, ExecutionContext}

trait ISiteRepository { this: MongoSoftDeleteRepository[Site, Site.ID] =>
  def findById(id: Site.ID): Future[Option[Site]]
  def findAll: Future[List[Site]]
  def findByDomain(domain: String): Future[Option[Site]]
  def findChildren(id: Option[String]): Future[Seq[Site]]
  def saveAndReload(site: Site): Future[Site]
  def delete(id: Site.ID): Future[Unit]
  def softDelete(id: Site.ID): Future[Unit]
}

class MongoSiteRepository(db: DefaultDB)(implicit ec: ExecutionContext, format: Format[Site], idWrites: Writes[Site.ID])
  extends MongoSoftDeleteRepository[Site, Site.ID](db)(ec, format, idWrites)
  with ISiteRepository {

  val collectionName = "cms_sites"

  def findByDomain(domain: String) = findOne(Json.obj("domain" -> domain))
  def findChildren(id: Option[Site.ID]) = id match {
    case Some(value) => find(Json.obj("parentId" -> value))
    case None        => find(Json.obj("parentId" -> Json.obj("$exists" -> false)))
  }
}