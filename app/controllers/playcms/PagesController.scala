package controllers.playcms

import play.api.mvc._
import play.api.libs.json.Writes
import playcms._
import playcms.models.Page
import playcms.services.IPageService
import scala.concurrent.ExecutionContext

class PagesController(pageService: IPageService)(implicit val executionContext: ExecutionContext)
  extends BaseController {

  import Page.pageFormats
  import Action._
  implicit val pageSeqWrites = Writes.seq[Page]

  def locationHeader(id: String) = LOCATION -> routes.PagesController.get(id).url

  def list = async {
    pageService.getAll.map(pages => Ok(json(pages)))
  }

  def get(id: String) = async {
    pageService.getById(id) map { maybePage =>
      maybePage.fold[SimpleResult](NotFound)(page => Ok(json(page)))
    }
  }

  def create = WSAction[Page] { page =>
    pageService.save(page) map (p => Created.withHeaders(locationHeader(p.id.get)))
  }

  def update(id: String) = WSAction[Page] { page =>
    pageService.save(page) map (p => ResetContent.withHeaders(locationHeader(id)))
  }

  def delete(id: String) = async {
    pageService.delete(id) map (_ => NoContent) recover {
      case e => BadRequest(e.toString)
    }
  }

  def uniqueCheck(id: Option[String], parentId: Option[String], relativePath: String) = async {
    pageService.isUnique(id, parentId, relativePath) map (isUnique => Ok(json(isUnique)))
  }
}

object PagesController extends PagesController(PageService)