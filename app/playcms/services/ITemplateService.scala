package playcms.services

import playcms.cache.ITemplateCache
import playcms.models.Template
import playcms.repository.ITemplateRepository
import playcms.util.Logging
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success}

trait ITemplateService {
  def get(name: String): Future[Option[Template]]
  def getById(id: String): Future[Option[Template]]
  def getAll: Future[Seq[Template]]
  def delete(id: String): Future[Unit]
  def save(site: Template): Future[Template]
}

//TODO: pub/sub
class TemplateService(repository: ITemplateRepository, cache: ITemplateCache)
                     (implicit val executionContext: ExecutionContext)
  extends ITemplateService
  with Logging {

  def get(name: String) = cache.getOrElse(name)(repository.findByName(name))
  def getById(id: String): Future[Option[Template]] = repository findById id
  def getAll: Future[Seq[Template]] = repository.findAll
  def delete(id: String): Future[Unit] = repository delete id
  def save(site: Template): Future[Template] = repository saveAndReload site
}
