package controllers.playcms

import play.api.data.validation.ValidationError
import play.api.i18n.Messages
import play.api.Logger
import play.api.mvc._
import play.api.libs.json._
import Action._
import Results._
import playcms.models.{RequestWithContent}
import scala.concurrent.{Future, ExecutionContext}
import scala.util.control.NonFatal

object WSAction {
  def apply[A](block: (RequestWithContent[A]) => Future[SimpleResult])
              (implicit reads: Reads[A], executionContext: ExecutionContext) = async { request =>

    request.body.asJson.map(reads.reads) match {
      case None => Future.successful(invalidBodyType)
      case Some(JsError(errors)) => Future.successful(invalidJson(errors))
      case Some(JsSuccess(data, _)) => recover(block(RequestWithContent(request, data)))
    }
  }

  /**
   * Web-Service action without a body, handles any errors.
   */
  def apply(block: => Future[SimpleResult])(implicit ec: ExecutionContext) = async { recover(block) }

  private def recover(f: Future[SimpleResult]) = f recover handleError

  def invalidBodyType = BadRequest(errorJson("expecting text/json or application/json body"))

  def invalidJson(validationErrors: Seq[(JsPath, Seq[ValidationError])]) = {
    val errorMessages = validationErrors map { case (path, errors) =>
      val messages = errors map (e => Messages(e.message, e.args))
      path.toString -> messages
    }
    val errorResult = errorJson("invalid json", errorMessages.toMap)
    BadRequest(errorResult)
  }

  private def handleError: PartialFunction[Throwable, SimpleResult] = {
    case NonFatal(e) => {
      Logger.error("web service failure", e)
      val result = InternalServerError(errorJson(e.getMessage))
      result
    }
  }

  private def errorJson(message: String, errors: Map[String, Seq[String]] = Map.empty): JsObject = {
    Json.obj(
      "message" -> message,
      "errors" -> errors
    )
  }
}