package controllers

import play.api.mvc._

object HomeController extends Controller {
  def index = Action {
    Ok("ok")
  }
}
