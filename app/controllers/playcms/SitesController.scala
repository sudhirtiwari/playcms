package controllers.playcms

import play.api.libs.json._
import play.api.mvc._
import playcms._
import playcms.models.Site
import playcms.services.ISiteService
import scala.concurrent.ExecutionContext

class SitesController(siteService: ISiteService)(implicit val executionContext: ExecutionContext, format: Format[Site])
  extends BaseController {

  implicit val siteSeqWrites = Writes.seq[Site]

  private def locationHeader(id: String) = LOCATION -> routes.SitesController.get(id).url

  def children(parentId: Option[String]) = WSAction {
    siteService.getByParentId(parentId) map (sites => Ok(json(sites)))
  }

  def get(id: String) = WSAction {
    siteService.getById(id) map { maybeSite =>
      maybeSite.fold[SimpleResult](NotFound)(site => Ok(json(site)))
    }
  }

  def create = WSAction[Site] { request =>
    siteService.save(request.content) map (s => Created.withHeaders(locationHeader(s.id.get)))
  }

  def update(id: String) = WSAction[Site] { request =>
    siteService.save(request.content) map (s => ResetContent.withHeaders(locationHeader(id)))
  }

  def delete(id: String) = WSAction {
    siteService.delete(id) map (_ => NoContent) recover {
      case e => BadRequest(e.toString)
    }
  }

  def uniqueCheck(id: Option[String], domain: String) = WSAction {
    siteService.isUnique(id, domain) map (isUnique => Ok(json(isUnique)))
  }
}

object SitesController extends SitesController(SiteService)