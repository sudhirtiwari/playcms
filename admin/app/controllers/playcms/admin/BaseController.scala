package controllers.playcms.admin

import play.api.libs.json.{Json, Writes}
import play.api.mvc.Controller

abstract class BaseController extends Controller {
  def json[A](a: A)(implicit writes: Writes[A]) = Json.toJson(a)
}
