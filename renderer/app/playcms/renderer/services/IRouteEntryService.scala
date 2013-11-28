package playcms.renderer.services

import playcms.cache.IRouteEntryCache
import playcms.events.{IEventSubscription, IEventBus}
import playcms.models.RouteEntry
import scala.concurrent.{ExecutionContext, Future}
import playcms.repository.IRouteEntryRepository

trait IRouteEntryService extends EventListeningService {
  def findRoute(fqdn: String, path: String): Future[Option[RouteEntry]]
}

//TOOD: register subscriptions
class RouteEntryService(cache: IRouteEntryCache, repository: IRouteEntryRepository, eventBus: IEventBus)
                       (implicit ec: ExecutionContext) extends IRouteEntryService {

  subscriptions = Nil

  def findRoute(fqdn: String, path: String) =
    cache.getOrElse(s"${fqdn.toLowerCase}:${path.toLowerCase}")(repository.findByAddress(fqdn, path))
}

