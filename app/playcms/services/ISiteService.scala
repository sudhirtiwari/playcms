package playcms.services

import playcms.cache.ISiteCache
import playcms.models.Site
import playcms.repository.ISiteRepository
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ISiteService {
  def get(domain: String)(implicit ec: ExecutionContext): Future[Option[Site]]
  def getById(id: String)(implicit ec: ExecutionContext): Future[Option[Site]]
  def getAll(implicit ec: ExecutionContext): Future[Seq[Site]]
  def delete(id: String)(implicit ec: ExecutionContext): Future[Unit]
  def save(site: Site)(implicit ec: ExecutionContext): Future[Site]
}

//TODO: pub/sub
class SiteService(repository: ISiteRepository, cache: ISiteCache)
  extends ISiteService {

  def get(domain: String)(implicit ec: ExecutionContext): Future[Option[Site]] =
    for {
      cached <- cache get domain
      loaded <- cached match {
        case found @ Some(_)  => Future successful found
        case None             => repository.findByDomain(domain) andThen {
          case Success(maybeLoaded) => maybeLoaded foreach (site => cache.set(site))
          case Failure(ex)          =>
        }
      }
    } yield loaded

  def getById(id: String)(implicit ec: ExecutionContext) = repository findById id
  def getAll(implicit ec: ExecutionContext) = repository.findAll
  def delete(id: String)(implicit ec: ExecutionContext) = repository delete id
  def save(site: Site)(implicit ec: ExecutionContext) = repository saveAndReload site
}
