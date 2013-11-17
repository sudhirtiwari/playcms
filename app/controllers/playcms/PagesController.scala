package controllers.playcms

import play.api.mvc._
import play.api.libs.json.{Format, Writes}
import playcms._
import playcms.models.Page
import playcms.services.IPageService
import scala.concurrent.ExecutionContext

class PagesController(pageService: IPageService)(implicit val executionContext: ExecutionContext, format: Format[Page])
  extends BaseController {

  implicit val pageSeqWrites = Writes.seq[Page]

  def locationHeader(id: String) = LOCATION -> routes.PagesController.get(id).url

  def children(siteId: String, parentId: Option[String]) = WSAction {
    pageService.getByParentId(siteId, parentId) map (pages => Ok(json(pages)))
  }

  def get(id: String) = WSAction {
    pageService.getById(id) map { maybePage =>
      maybePage.fold[SimpleResult](NotFound)(page => Ok(json(page)))
    }
  }

  def create = WSAction[Page] { request =>
    pageService.save(request.content) map (p => Created.withHeaders(locationHeader(p.id.get)))
  }

  def update(id: String) = WSAction[Page] { request =>
    pageService.save(request.content) map (p => ResetContent.withHeaders(locationHeader(id)))
  }

  def delete(id: String) = WSAction {
    pageService.delete(id) map (_ => NoContent)
  }

  def uniqueCheck(id: Option[String], siteId: String, parentId: Option[String], relativePath: String) = WSAction {
    pageService.isUnique(id, siteId, parentId, relativePath) map (isUnique => Ok(json(isUnique)))
  }
}

object PagesController extends PagesController(PageService)