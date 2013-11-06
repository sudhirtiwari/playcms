package playcms.services

import playcms.cache.ITemplateCache
import playcms.models.Template
import playcms.repository.ITemplateRepository
import playcms.services.events.{TemplateDeletedEvent, TemplateUpdatedEvent, TemplateAddedEvent, IEventBus}
import playcms.util.Logging
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success

trait ITemplateService {
  def get(name: String): Future[Option[Template]]
  def getById(id: String): Future[Option[Template]]
  def getAll: Future[Seq[Template]]
  def delete(id: String): Future[Unit]
  def save(site: Template): Future[Template]
  def isUnique(id: Option[String], name: String): Future[Boolean]
}

class TemplateService(repository: ITemplateRepository, cache: ITemplateCache, eventBus: IEventBus)
                     (implicit val executionContext: ExecutionContext)
  extends ITemplateService
  with Logging {

  eventBus.subscribe(TemplateAddedEvent) {
    case TemplateAddedEvent(template) => cache set template
  }

  eventBus.subscribe(TemplateUpdatedEvent) {
    case TemplateUpdatedEvent(template) => cache.setIfExists(template.id.get, template)
  }

  eventBus.subscribe(TemplateDeletedEvent) {
    case TemplateDeletedEvent(id) => cache removeByKey id
  }

  def get(name: String) = repository.findByName(name)
  def getById(id: String) = cache.getOrElse(id)(repository.findById(id))
  def getAll: Future[Seq[Template]] = repository.findAll

  def delete(id: String): Future[Unit] = repository.delete(id) andThen {
    case Success(_) => eventBus.publish(TemplateDeletedEvent(id))
  }

  def save(template: Template): Future[Template] = repository.saveAndReload(template) andThen {
    case Success(reloaded) => template.id match {
      case Some(_) => eventBus.publish(TemplateUpdatedEvent(reloaded))
      case None    => eventBus.publish(TemplateAddedEvent(reloaded))
    }
  }

  def isUnique(id: Option[String], name: String) = repository.findByName(name) map {
    case Some(template) => id.isDefined && id.get == template.id.get
    case None           => true
  }
}
