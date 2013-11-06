package controllers.playcms

import play.api.mvc.Action

object DashboardController extends BaseController {
  def index = Action {
    Ok(views.html.playcms.pages.index())
  }
}
