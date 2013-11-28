package controllers.playcms.admin

import play.api.mvc._
import play.api.libs.json.{Format, Writes}
import playcms.admin._
import playcms.models.{Site, Page}
import playcms.admin.services.IPageService
import scala.concurrent.ExecutionContext

class PagesController(pageService: IPageService)(implicit executionContext: ExecutionContext, format: Format[Page])
  extends BaseController {

  implicit val pageSeqWrites = Writes.seq[Page]

  def locationHeader(id: Page.ID) = LOCATION -> routes.PagesController.get(id).url

  def children(siteId: Site.ID, parentId: Option[Page.ID]) = WSAction {
    pageService.getByParentId(siteId, parentId) map (pages => Ok(json(pages)))
  }

  def get(id: Page.ID) = WSAction {
    pageService.getById(id) map { maybePage =>
      maybePage.fold[SimpleResult](NotFound)(page => Ok(json(page)))
    }
  }

  def create = WSAction[Page] { request =>
    pageService.save(request.content) map (p => Created.withHeaders(locationHeader(p.id.get)))
  }

  def update(id: Page.ID) = WSAction[Page] { request =>
    pageService.save(request.content) map (p => ResetContent.withHeaders(locationHeader(id)))
  }

  def delete(id: Page.ID) = WSAction {
    pageService.delete(id) map (_ => NoContent)
  }

  def uniqueCheck(id: Option[Page.ID], siteId: Site.ID, parentId: Option[Page.ID], relativePath: String) = WSAction {
    pageService.isUnique(id, siteId, parentId, relativePath) map (isUnique => Ok(json(isUnique)))
  }
}

object PagesController extends PagesController(PageService)