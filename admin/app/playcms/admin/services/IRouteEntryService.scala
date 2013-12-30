package playcms.admin.services

import play.api.libs.json.Writes
import play.api.http.Status._
import playcms.models.{RouteEntry, Page}
import playcms.repository.IRouteEntryRepository
import playcms.events.IEventBus
import playcms.util._
import scala.concurrent.{ExecutionContext, Future}

trait IRouteEntryService {
  def addRoute(page: Page): Future[Boolean]
  def removeRoute(route: RouteEntry): Future[Boolean]
  def findRoute(fqdn: String, path: String): Future[Option[RouteEntry]]
  def moveTemporarily(page: Page, path: String, fqdn: Option[String]): Future[Boolean]
  //def movePermanently(page: Page): Future[Boolean]
  def gone(pageId: Page.ID): Future[Boolean]
}

//TODO: publish events
class RouteEntryService(routeEntryRepository: IRouteEntryRepository, siteService: ISiteService, eventBus: IEventBus)
                       (implicit val executionContext: ExecutionContext, routeEntryWrites: Writes[RouteEntry])
  extends IRouteEntryService with Logging {

  def addRoute(page: Page): Future[Boolean] =
    for {
      maybeSite <- siteService.getById(page.siteId)
      fqdn = maybeSite.flatMap(_.domain).get
      route = RouteEntry(
        id = None,
        fqdn = fqdn,
        path = page.fullPath.getOrElse(page.relativePath),
        pageId = page.id,
        status = OK,
        redirectTo =  None)
      result <- routeEntryRepository.saveAndReload(route) fold (
        onSuccess = { _ => true },
        onFailure = { t =>
          error(s"Unable to add route: $route", t)
          false
        })
    } yield result

  def removeRoute(route: RouteEntry): Future[Boolean] =
    routeEntryRepository.delete(route.id.get) fold (
      onSuccess = { _ => true },
      onFailure = { t =>
        error(s"Unable to remove route: $route", t)
        false
      }
    )

  def findRoute(fqdn: String, path: String): Future[Option[RouteEntry]] =
    routeEntryRepository.findByAddress(fqdn, path)

  def moveTemporarily(page: Page, path: String, fqdn: Option[String]): Future[Boolean] = {
    val result = for {
      routes <- routeEntryRepository.findByPageId(page.id.get)
      updated = routes map {
        case entry @ RouteEntry(_, _, _, _, OK, _) =>
          val newFQDN = fqdn getOrElse entry.fqdn
          entry.copy(fqdn = newFQDN, path = page.fullPath.get)
        case entry @ RouteEntry(_, _, _, _, MOVED_PERMANENTLY, _) =>
        case entry @ RouteEntry(_, _, _, _, TEMPORARY_REDIRECT, _) =>
        case entry => entry
      }
    } yield updated

    result fold (
      onSuccess = { _ => true },
      onFailure = { t => false }
    )
  }

  //def movePermanently(page: Page): Future[Boolean] = ???

  def gone(pageId: Page.ID): Future[Boolean] =
    (for {
      routes <- routeEntryRepository.findByPageId(pageId)
      updatedRoutes = routes map (_.copy(status = GONE))
      result <- Future.traverse(updatedRoutes)(routeEntryRepository.saveAndReload)
    } yield result) fold (
      onSuccess = { _ => true },
      onFailure = { t =>
        error(s"Unable to mark routes with pageId: $pageId as GONE", t)
        false
      }
    )
}



