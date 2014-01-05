package controllers.playcms.admin

import play.api.mvc.{Action, Controller}
import play.api.Routes

object Application extends Controller {
  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.PagesController.get,
        routes.javascript.PagesController.create,
        routes.javascript.PagesController.update,
        routes.javascript.PagesController.delete,
        routes.javascript.PagesController.uniqueCheck,
        routes.javascript.PagesController.children,
        routes.javascript.SitesController.get,
        routes.javascript.SitesController.create,
        routes.javascript.SitesController.update,
        routes.javascript.SitesController.delete,
        routes.javascript.SitesController.uniqueCheck,
        routes.javascript.SitesController.children,
        routes.javascript.TemplatesController.list,
        routes.javascript.TemplatesController.get,
        routes.javascript.TemplatesController.create,
        routes.javascript.TemplatesController.update,
        routes.javascript.TemplatesController.delete,
        routes.javascript.TemplatesController.uniqueCheck
      )
    ).as("text/javascript")
  }
}
