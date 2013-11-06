package controllers.playcms

import play.api.mvc._
import play.api.libs.json._
import Results._
import scala.concurrent.Future

object WSAction {

  def apply[A](block: A => Future[SimpleResult])(implicit reads: Reads[A]) = Action.async { request =>
    request.body.asJson match {
      case Some(json) => json.validate[A].fold(
        valid = block,
        invalid = { e => Future.successful(BadRequest(e.toString()))}
      )
      case None => Future.successful(BadRequest("Missing or malformed JSON content"))
    }
  }
}
