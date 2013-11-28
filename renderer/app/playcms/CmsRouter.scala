package playcms

import play.api.mvc.{Handler, RequestHeader}
import playcms.renderer.services.{IPageService, IRouteEntryService}
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

//TODO: configure a timeout
class CmsRouter(router: IRouteEntryService, pageService: IPageService)(implicit ec: ExecutionContext) {
  def onRouteRequest(request: RequestHeader): Option[Handler] =
    Await.result(for {
      maybeRoute <- router.findRoute(request.host, request.path)
      maybeHandler = maybeRoute map (controllers.playcms.renderer.PageRendererController.render(_))
    } yield maybeHandler, Duration.Inf)
}
