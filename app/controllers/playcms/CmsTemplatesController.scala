package controllers.playcms

import play.api.mvc._

object CmsTemplatesController extends Controller {
  def index = Action {
    Ok(views.html.templates.index())
  }
}
