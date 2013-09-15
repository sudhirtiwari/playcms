package controllers.playcms

import play.api.mvc._

object CmsPagesController extends Controller {
  def index = Action {
    Ok(views.html.pages.index())
  }
}