import play.api.{Application, GlobalSettings}
import play.api.mvc.RequestHeader
import playcms.renderer._

class PlayCmsGlobal extends GlobalSettings {

  override def onRouteRequest(request: RequestHeader) =
    super.onRouteRequest(request) orElse CmsRouter.onRouteRequest(request)

  override def onStop(app: Application): Unit = {
    PageService.dispose()
    RouteEntryService.dispose()
    SiteService.dispose()
    TemplateService.dispose()

    super.onStop(app)
  }
}
