package playcms.admin.services

import play.api.libs.json.Writes
import playcms.models.Template
import playcms.repository.ITemplateRepository
import playcms.events.{TemplateDeletedEvent, TemplateUpdatedEvent, TemplateAddedEvent, IEventBus}
import playcms.util.Logging
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success

trait ITemplateService {
  def get(name: String): Future[Option[Template]]
  def getById(id: Template.ID): Future[Option[Template]]
  def getAll: Future[Seq[Template]]
  def delete(id: Template.ID): Future[Unit]
  def save(site: Template): Future[Template]
  def isUnique(id: Option[Template.ID], name: String): Future[Boolean]
}

class TemplateService(repository: ITemplateRepository, eventBus: IEventBus)
                     (implicit val executionContext: ExecutionContext, templateWrites: Writes[Template])
  extends ITemplateService
  with Logging {

  def get(name: String) = repository.findByName(name)
  def getById(id: Template.ID) = repository.findById(id)
  def getAll: Future[Seq[Template]] = repository.findAll

  def delete(id: Template.ID): Future[Unit] = repository.delete(id) andThen {
    case Success(_) => eventBus.publish(TemplateDeletedEvent(id))
  }

  def save(template: Template): Future[Template] = repository.saveAndReload(template) andThen {
    case Success(reloaded) => template.id match {
      case Some(_) => eventBus.publish(TemplateUpdatedEvent(reloaded))
      case None    => eventBus.publish(TemplateAddedEvent(reloaded))
    }
  }

  def isUnique(id: Option[Template.ID], name: String) = repository.findByName(name) map {
    case Some(template) => id.isDefined && id.get == template.id.get
    case None           => true
  }
}
