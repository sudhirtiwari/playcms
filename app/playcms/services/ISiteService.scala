package playcms.services

import playcms.cache.ISiteCache
import playcms.models.Site
import playcms.repository.ISiteRepository
import playcms.services.events.{SiteDeletedEvent, SiteUpdatedEvent, SiteAddedEvent, IEventBus}
import playcms.util.Logging
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}

trait ISiteService {
  def get(domain: String): Future[Option[Site]]
  def getById(id: String): Future[Option[Site]]
  def getAll: Future[Seq[Site]]
  def delete(id: String): Future[Unit]
  def softDelete(id: String): Future[Unit]
  def save(site: Site): Future[Site]
  def walkParents(site: Site): Future[Seq[Site]]
  def isUnique(id: Option[String], domain: String): Future[Boolean]
}

class SiteService(repository: ISiteRepository, cache: ISiteCache, eventBus: IEventBus, pageService: IPageService)
                 (implicit val ec: ExecutionContext)

  extends ISiteService
  with Logging {

  eventBus.subscribe(SiteAddedEvent) {
    case SiteAddedEvent(site) => cache set site
  }

  eventBus.subscribe(SiteUpdatedEvent) {
    case SiteUpdatedEvent(site) => cache.setIfExists(site.id.get, site)
  }

  eventBus.subscribe(SiteDeletedEvent) {
    case SiteDeletedEvent(id) => cache removeByKey id
  }

  def get(domain: String) = cache.getOrElse(domain)(repository.findByDomain(domain))
  def getById(id: String) = repository findById id
  def getAll = repository.findAll
  def delete(id: String): Future[Unit] =
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

  def softDelete(id: String) =
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
      domain = parents map (_.name) mkString(".")
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

  def isUnique(id: Option[String], domain: String) = {
    repository.findByDomain(domain) map {
      case Some(site) => id.isDefined && id.get == site.id.get
      case None       => true
    }
  }
}
