package playcms.repository

import play.api.libs.json.{Json, Format}
import playcms.models.Site
import reactivemongo.api.DefaultDB
import scala.concurrent.{Future, ExecutionContext}

trait ISiteRepository { this: MongoSoftDeleteRepository[Site] =>
  def findById(id: String): Future[Option[Site]]
  def findAll: Future[List[Site]]
  def findByDomain(domain: String): Future[Option[Site]]
  def findChildren(id: Option[String]): Future[Seq[Site]]
  def saveAndReload(site: Site): Future[Site]
  def delete(id: String): Future[Unit]
  def softDelete(id: String): Future[Unit]
}

class MongoSiteRepository(db: DefaultDB)(override implicit val ec: ExecutionContext, format: Format[Site])
  extends MongoSoftDeleteRepository[Site](db)
  with ISiteRepository {

  val collectionName = "cms_sites"

  def findByDomain(domain: String) = findOne(Json.obj("domain" -> domain))
  def findChildren(id: Option[String]) = id match {
    case Some(value) => find(Json.obj("parentId" -> value))
    case None        => find(Json.obj("parentId" -> Json.obj("$exists" -> false)))
  }
}