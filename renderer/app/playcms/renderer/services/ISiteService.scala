package playcms.renderer.services

import playcms.cache.ISiteCache
import playcms.events._

trait ISiteService extends EventListeningService

class SiteService(cache: ISiteCache, eventBus: IEventBus) extends ISiteService {
  subscriptions = List(
    eventBus.subscribe(SiteAddedEvent) {
      case SiteAddedEvent(site) => cache set site
    },

    eventBus.subscribe(SiteUpdatedEvent) {
      case SiteUpdatedEvent(site) => cache.setIfExists(site.id.get, site)
    },

    eventBus.subscribe(SiteDeletedEvent) {
      case SiteDeletedEvent(id) => cache removeByKey id
    }
  )
}
