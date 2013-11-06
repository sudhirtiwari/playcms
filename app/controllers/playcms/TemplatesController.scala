package controllers.playcms

import play.api.mvc._
import playcms._
import playcms.models.Template
import playcms.services.ITemplateService
import scala.concurrent.ExecutionContext
import play.api.libs.json.Writes

class TemplatesController(templateService: ITemplateService)(implicit val executionContext: ExecutionContext)
  extends BaseController {

  import Template.templateFormats
  import Action._
  implicit val templateSeqWrites = Writes.seq[Template]

  def list = async {
    templateService.getAll.map(templates => Ok(json(templates)))
  }

  def get(id: String) = async {
    templateService.getById(id) map { maybeTemplate =>
      maybeTemplate.fold[SimpleResult](NotFound)(template => Ok(json(template)))
    }
  }

  def create = WSAction[Template] { template =>
    templateService.save(template) map (t => Created.withHeaders())
  }

  def update(id: String) = WSAction[Template] { template =>
    templateService.save(template) map (t => NoContent.withHeaders())
  }

  def delete(id: String) = async {
    templateService.delete(id) map (_ => NoContent) recover {
      case e => BadRequest(e.toString)
    }
  }

  def uniqueCheck(id: Option[String], name: String) = async {
    templateService.isUnique(id, name) map (isUnique => Ok(json(isUnique)))
  }
}

object TemplatesController extends TemplatesController(TemplateService)
