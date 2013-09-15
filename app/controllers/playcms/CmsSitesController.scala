package controllers.playcms

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import playcms.models.Site
import playcms.repository.ISiteRepository

class CmsSitesController(siteRepository: ISiteRepository)
  extends Controller {

  implicit val siteFormat = (
    (__ \ "id").formatNullable[String] ~
    (__ \ "title").format[String] ~
    (__ \ "description").formatNullable[String] ~
    (__ \ "domain").format[String]
  )(Site.apply, unlift(Site.unapply))

  def list = Action { request =>
    Async {
      siteRepository.findAll.map { sites =>
        Ok(Json.toJson(sites)(Writes.traversableWrites[Site]))
      }
    }
  }

  def get(id: String) = Action { request =>
    Async {
      siteRepository.findById(id) map { maybeSite =>
        maybeSite.fold[Result](Status(NOT_FOUND)) { site =>
          Ok(Json.toJson(site))
        }
      }
    }
  }

  def save = Action(parse.json) { request =>
    request.body.validate[Site].fold(
      valid = { res => Async(siteRepository.saveAndReload(res) map (site => Ok(Json.toJson(site)))) },
      invalid = { e => BadRequest(e.toString()) }
    )
  }

  def delete(id: String) = Action {
    Async {
      siteRepository.delete(id) map (_ => Status(NO_CONTENT)) recover {
        case e => BadRequest(e.toString)
      }
    }
  }

  def uniqueCheck(id: Option[String], domain: String) = Action {
    Async {
      siteRepository.isUnique(id, domain) map { isUnique =>
        Ok(Json.toJson(isUnique))
      }
    }
  }
}
