package playcms

import play.api.mvc.{Handler, RequestHeader}

//TODO: Look-up pages in CMS
class CmsRouter() {
  def onRouteRequest(request: RequestHeader): Option[Handler] = {

    None
  }
}
