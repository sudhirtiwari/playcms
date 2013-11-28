package playcms.renderer.services

import playcms.cache.ITemplateCache
import playcms.events._
import playcms.models.Template
import scala.concurrent.Future
import playcms.repository.ITemplateRepository

trait ITemplateService extends EventListeningService {
  def getById(templateId: Template.ID): Future[Option[Template]]
}

class TemplateService(cache: ITemplateCache, repository: ITemplateRepository, eventBus: IEventBus)
  extends ITemplateService {

  subscriptions = List(
    eventBus.subscribe(TemplateAddedEvent) {
      case TemplateAddedEvent(template) => cache set template
    },

    eventBus.subscribe(TemplateUpdatedEvent) {
      case TemplateUpdatedEvent(template) => cache.setIfExists(template.id.get, template)
    },

    eventBus.subscribe(TemplateDeletedEvent) {
      case TemplateDeletedEvent(id) => cache removeByKey id
    }
  )

  def getById(templateId: Template.ID) = cache.getOrElse(templateId)(repository.findById(templateId))
}
