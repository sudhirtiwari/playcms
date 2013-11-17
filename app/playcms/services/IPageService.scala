package playcms.services

import playcms.cache.IPageCache
import playcms.models.Page
import playcms.repository.IPageRepository
import playcms.services.events.{PageUpdatedEvent, PageAddedEvent, PageDeletedEvent, IEventBus}
import playcms.util.UrlHelper._
import scala.concurrent.{Future, ExecutionContext}

trait IPageService {
  def getById(id: String): Future[Option[Page]]
  def getBySite(siteId: String): Future[Seq[Page]]
  def getByParentId(siteId: String, parentId: Option[String]): Future[Seq[Page]]
  def getAll: Future[Seq[Page]]
  def delete(id: String): Future[Unit]
  def softDelete(id: String): Future[Unit]
  def save(page: Page): Future[Page]
  def walkParents(page: Page): Future[Seq[Page]]
  def isUnique(id: Option[String], siteId: String, parentId: Option[String], relativePath: String): Future[Boolean]
}

class PageService(repository: IPageRepository, cache: IPageCache, eventBus: IEventBus)
                 (implicit ec: ExecutionContext) extends IPageService {

  eventBus.subscribe(PageAddedEvent) {
    case PageAddedEvent(page) => cache set page
  }

  eventBus.subscribe(PageUpdatedEvent) {
    case PageUpdatedEvent(page) => cache.setIfExists(page.id.get, page)
  }

  eventBus.subscribe(PageDeletedEvent) {
    case PageDeletedEvent(id) => cache.removeByKey(id)
  }

  def getById(id: String) = cache.getOrElse(id)(repository.findById(id))
  def getBySite(siteId: String) = repository.findBySite(siteId)
  def getByParentId(siteId: String, parentId: Option[String]) = repository.findChildren(siteId, parentId)
  def getAll = repository.findAll
  def delete(id: String) =
    for {
      page <- repository.findById(id)
      children <- page map (p => repository.findChildren(p.siteId, p.id)) getOrElse Future.successful(Nil)
      _ <- Future.traverse(children)(child => delete(child.id.get)) map (_ => {})
      parentDeleted <- repository.delete(id)
    } yield {
      eventBus.publish(PageDeletedEvent(id))
      parentDeleted
    }

  def softDelete(id: String) =
    for {
      page <- repository.findById(id)
      children <- page map (p => repository.findChildren(p.siteId, p.id)) getOrElse Future.successful(Nil)
      parentDeleted <- repository.softDelete(id)
    } yield {
      eventBus.publish(PageDeletedEvent(id))
      parentDeleted
    }

  def save(page: Page): Future[Page] =
    for {
      parents <- walkParents(page)
      fullPath = url("", parents map (_.relativePath) :_*)
      reloaded <- repository.saveAndReload(page.copy(fullPath = Some(fullPath)))
    } yield {
      page.id match {
        case Some(_) => eventBus.publish(PageUpdatedEvent(page))
        case None    => eventBus.publish(PageAddedEvent(page))
      }
      reloaded
    }

  def walkParents(page: Page): Future[Seq[Page]] = {
    def step(page: Page)(results: Seq[Page] = Seq.empty[Page]): Future[Seq[Page]] =
      page.parentId match {
        case Some(parentId) => getById(parentId) flatMap {
          case Some(parent) => step(parent)(results :+ page)
          case None         => Future.successful(results :+ page)
        }
        case None           => Future.successful(results :+ page)
      }

    step(page)()
  }

  def isUnique(id: Option[String], siteId: String, parentId: Option[String], relativePath: String) =
    repository.findChildren(siteId, parentId) map { peers =>
      peers.filter({ page =>
        page.relativePath.toLowerCase == relativePath.toLowerCase && page.id.get != id.get
      }).isEmpty
    }
}