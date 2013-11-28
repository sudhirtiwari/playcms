package controllers.playcms.renderer

import play.api.mvc.{Controller, Action}
import playcms.models.{Page, RouteEntry}
import playcms.renderer._
import playcms.util.UrlHelper._
import scala.concurrent.{ExecutionContext, Future}
import playcms.renderer.services.{ITemplateService, IPageService}

class PageRendererController(pageService: IPageService, templateService: ITemplateService)
                            (implicit executionContext: ExecutionContext) extends Controller {

  def render(route: RouteEntry) = Action.async { implicit request =>
    route.status match {
      case SEE_OTHER          =>  Future.successful(SeeOther(url(route.fqdn, route.path)))
      case MOVED_PERMANENTLY  =>  Future.successful(MovedPermanently(url(route.fqdn, route.path)))
      case TEMPORARY_REDIRECT =>  Future.successful(TemporaryRedirect(url(route.fqdn, route.path)))
      case GONE               =>  Future.successful(Gone)
      case OK                 =>  Future.successful(Ok)
    }
  }

//  private def renderPage(pageId: Page.ID) =
//    for {
//      maybePage <- pageService.getById(pageId)
//      template <- maybePage match {
//        case Some(page) => templateService.getById(page.templateId)
//        case None => Future.successful(None)
//      }
//    }
}

object PageRendererController extends PageRendererController()
