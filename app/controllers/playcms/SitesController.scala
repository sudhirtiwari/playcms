package controllers.playcms

import play.api.libs.json._
import play.api.mvc._
import playcms._
import playcms.models.Site
import playcms.services.ISiteService
import scala.concurrent.ExecutionContext

class SitesController(siteService: ISiteService)(implicit val executionContext: ExecutionContext)
  extends BaseController {

  import Site.siteFormat
  import Action._
  implicit val siteSeqWrites = Writes.seq[Site]

  private def locationHeader(id: String) = LOCATION -> routes.SitesController.get(id).url

  def children(parentId: Option[String]) = async {
    siteService.getByParentId(parentId) map (sites => Ok(json(sites)))
  }

  def get(id: String) = async {
    siteService.getById(id) map { maybeSite =>
      maybeSite.fold[SimpleResult](NotFound)(site => Ok(json(site)))
    }
  }

  def create = WSAction[Site] { site =>
    siteService.save(site) map (s => Created.withHeaders(locationHeader(s.id.get)))
  }

  def update(id: String) = WSAction[Site] { site =>
    siteService.save(site) map (s => ResetContent.withHeaders(locationHeader(id)))
  }

  def delete(id: String) = async {
    siteService.delete(id) map (_ => NoContent) recover {
      case e => BadRequest(e.toString)
    }
  }

  def uniqueCheck(id: Option[String], domain: String) = async {
    siteService.isUnique(id, domain) map (isUnique => Ok(json(isUnique)))
  }
}

object SitesController extends SitesController(SiteService)