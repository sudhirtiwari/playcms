import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

object Global
  extends GlobalSettings {

  override def onStart(app: Application) {
    Logger info "Application has started"
  }

  override def onStop(app: Application) {
    Logger info "Application has stopped"
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    Logger error (ex.getMessage, ex)
    Future.successful(InternalServerError(ex.getMessage))
  }
}