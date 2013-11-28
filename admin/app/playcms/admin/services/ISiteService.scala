package playcms.admin.services

import play.api.libs.json.Writes
import playcms.models.Site
import playcms.repository.ISiteRepository
import playcms.events.{SiteDeletedEvent, SiteUpdatedEvent, SiteAddedEvent, IEventBus}
import playcms.util.Logging
import scala.concurrent.{ExecutionContext, Future}

trait ISiteService {
  def get(domain: String): Future[Option[Site]]
  def getById(id: Site.ID): Future[Option[Site]]
  def getByParentId(parentId: Option[Site.ID]): Future[Seq[Site]]
  def getAll: Future[Seq[Site]]
  def delete(id: Site.ID): Future[Unit]
  def softDelete(id: Site.ID): Future[Unit]
  def save(site: Site): Future[Site]
  def walkParents(site: Site): Future[Seq[Site]]
  def isUnique(id: Option[Site.ID], domain: String): Future[Boolean]
}

class SiteService(repository: ISiteRepository, eventBus: IEventBus, pageService: IPageService)
                 (implicit val ec: ExecutionContext, siteWrites: Writes[Site])

  extends ISiteService
  with Logging {

  def get(domain: String) = repository.findByDomain(domain)
  def getById(id: Site.ID) = repository findById id
  def getByParentId(parentId: Option[Site.ID]): Future[Seq[Site]] = repository findChildren parentId
  def getAll = repository.findAll
  def delete(id: Site.ID): Future[Unit] =
    for {
      pages <- pageService.getBySite(id)
      _ <- Future.traverse(pages)(page => pageService.delete(page.id.get))
      children <- repository.findChildren(Some(id))
      _ <- Future.traverse(children)(child => delete(child.id.get)) map (_ => {})
      parentDeleted <- repository delete id
    } yield {
      eventBus.publish(SiteDeletedEvent(id))
      parentDeleted
    }

  def softDelete(id: Site.ID) =
    for {
      pages <- pageService.getBySite(id)
      _ <- Future.traverse(pages)(page => pageService.softDelete(page.id.get))
      children <- repository.findChildren(Some(id))
      _ <- Future.traverse(children)(child => softDelete(child.id.get)) map (_ => {})
      parentDeleted <- repository softDelete id
    } yield {
      eventBus.publish(SiteDeletedEvent(id))
      parentDeleted
    }

  def save(site: Site) =
    for {
      parents <- walkParents(site)
      domain = parents.map(_.name).mkString(".")
      reloaded <- repository.saveAndReload(site.copy(domain = Some(domain)))
    } yield {
      site.id match {
        case Some(_) => eventBus.publish(SiteUpdatedEvent(reloaded))
        case None    => eventBus.publish(SiteAddedEvent(reloaded))
      }
      reloaded
    }

  def walkParents(site: Site): Future[Seq[Site]] = {
    def step(site: Site)(results: Seq[Site] = Seq.empty[Site]): Future[Seq[Site]] =
      site.parentId match {
        case Some(parentId) => getById(parentId) flatMap {
          case Some(parent) => step(parent)(results :+ site)
          case None         => Future.successful(results :+ site)
        }
        case None           => Future.successful(results :+ site)
      }

    step(site)()
  }

  def isUnique(id: Option[Site.ID], domain: String) =
    repository.findByDomain(domain) map {
      case Some(site) => id.isDefined && id.get == site.id.get
      case None       => true
    }
}
