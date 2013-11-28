package playcms.admin.services

import play.api.libs.json.Writes
import playcms.models.{Site, Page}
import playcms.events.{PageUpdatedEvent, PageAddedEvent, PageDeletedEvent, IEventBus}
import playcms.repository.IPageRepository
import playcms.util.UrlHelper._
import scala.concurrent.{Future, ExecutionContext}

trait IPageService {
  def getById(id: Page.ID): Future[Option[Page]]
  def getBySite(siteId: Site.ID): Future[Seq[Page]]
  def getByParentId(siteId: Site.ID, parentId: Option[Page.ID]): Future[Seq[Page]]
  def getAll: Future[Seq[Page]]
  def delete(id: Page.ID): Future[Unit]
  def softDelete(id: Page.ID): Future[Unit]
  def save(page: Page): Future[Page]
  def walkParents(page: Page): Future[Seq[Page]]
  def isUnique(id: Option[Page.ID], siteId: Site.ID, parentId: Option[Page.ID], relativePath: String): Future[Boolean]
}

class PageService(repository: IPageRepository, eventBus: IEventBus)
                 (implicit ec: ExecutionContext, pageWrites: Writes[Page]) extends IPageService {

  def getById(id: Page.ID) = repository.findById(id)
  def getBySite(siteId: Site.ID) = repository.findBySite(siteId)
  def getByParentId(siteId: Site.ID, parentId: Option[Page.ID]) = repository.findChildren(siteId, parentId)
  def getAll = repository.findAll
  def delete(id: Page.ID) =
    for {
      page <- repository.findById(id)
      children <- page map (p => repository.findChildren(p.siteId, p.id)) getOrElse Future.successful(Nil)
      _ <- Future.traverse(children)(child => delete(child.id.get)) map (_ => {})
      parentDeleted <- repository.delete(id)
    } yield {
      eventBus.publish(PageDeletedEvent(id))
      parentDeleted
    }

  def softDelete(id: Page.ID) =
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

  def isUnique(id: Option[Page.ID], siteId: Site.ID, parentId: Option[Page.ID], relativePath: String) =
    repository.findChildren(siteId, parentId) map { peers =>
      peers.filter({ page =>
        page.relativePath.toLowerCase == relativePath.toLowerCase && page.id.get != id.get
      }).isEmpty
    }
}