package playcms.renderer.services

import playcms.cache.IPageCache
import playcms.events._
import playcms.models.Page
import scala.concurrent.Future
import playcms.repository.IPageRepository

trait IPageService extends EventListeningService {
  def getById(id: Page.ID): Future[Option[Page]]
}

class PageService(cache: IPageCache, repository: IPageRepository, eventBus: IEventBus) extends IPageService {
  subscriptions = List(
    eventBus.subscribe(PageAddedEvent) {
      case PageAddedEvent(page) => cache set page
    },

    eventBus.subscribe(PageUpdatedEvent) {
      case PageUpdatedEvent(page) => cache.setIfExists(page.id.get, page)
    },

    eventBus.subscribe(PageDeletedEvent) {
      case PageDeletedEvent(id) => cache.removeByKey(id)
    }
  )

  def getById(id: Page.ID) = cache.getOrElse(id)(repository.findById(id))
}
