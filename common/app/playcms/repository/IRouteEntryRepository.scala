package playcms.repository

import play.api.libs.json.{Writes, Json, Format}
import playcms.models.{Page, RouteEntry}
import playcms.util.UrlHelper._
import reactivemongo.api.DefaultDB
import scala.concurrent.{ExecutionContext, Future}

trait IRouteEntryRepository { this: MongoRepository[RouteEntry, RouteEntry.ID] =>
  def findById(id: RouteEntry.ID): Future[Option[RouteEntry]]
  def findByPageId(pageId: Page.ID): Future[Seq[RouteEntry]]
  def findByAddress(fqdn: String, path: String): Future[Option[RouteEntry]]
  def saveAndReload(routeEntry: RouteEntry): Future[RouteEntry]
  def delete(id: RouteEntry.ID): Future[Unit]
}

class MongoRouteEntryRepository(db: DefaultDB)
                               (implicit ec: ExecutionContext, format: Format[RouteEntry], idWrites: Writes[RouteEntry.ID])
  extends MongoRepository[RouteEntry, RouteEntry.ID](db)(ec, format, idWrites)
  with IRouteEntryRepository {

  val collectionName = "route_entries"

  def findByPageId(pageId: Page.ID): Future[Seq[RouteEntry]] = find(Json.obj("pageId" -> pageId))
  def findByAddress(fqdn: String, path: String): Future[Option[RouteEntry]] =
    findOne(Json.obj("fqdn" -> fqdn, "path" -> url("", path)))
}
