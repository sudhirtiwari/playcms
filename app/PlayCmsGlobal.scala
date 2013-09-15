import play.api.GlobalSettings
import play.api.mvc.RequestHeader
import playcms.CmsRouter

class PlayCmsGlobal extends GlobalSettings {

  override def onRouteRequest(request: RequestHeader) = {
    super.onRouteRequest(request) orElse {
      new CmsRouter().onRouteRequest(request)
    }
  }
}
