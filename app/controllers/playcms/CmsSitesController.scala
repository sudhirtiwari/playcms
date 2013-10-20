package controllers.playcms

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import playcms.models.Site
import playcms.services.ISiteService
import scala.concurrent.Future

class CmsSitesController(siteService: ISiteService)
  extends Controller {

  import Site.siteFormat
  import Action._

  def list = async { request =>
    siteService.getAll.map { sites =>
      Ok(Json.toJson(sites)(Writes.traversableWrites[Site]))
    }
  }

  def get(id: String) = async { request =>
    siteService.getById(id) map { maybeSite =>
      maybeSite.fold[SimpleResult](Status(NOT_FOUND)) { site =>
        Ok(Json.toJson(site))
      }
    }
  }

  def save = async(parse.json) { request =>
    request.body.validate[Site].fold(
      valid = { res => siteService.save(res) map (site => Ok(Json.toJson(site))) },
      invalid = { e => Future.successful(BadRequest(e.toString())) }
    )
  }

  def delete(id: String) = async {
    siteService.delete(id) map (_ => Status(NO_CONTENT)) recover {
      case e => BadRequest(e.toString)
    }
  }

  def uniqueCheck(id: Option[String], domain: String) = async {
    siteService.isUnique(id, domain) map { isUnique =>
      Ok(Json.toJson(isUnique))
    }
  }
}
