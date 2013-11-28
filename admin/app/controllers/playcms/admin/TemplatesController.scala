package controllers.playcms.admin

import play.api.libs.json.{Format, Writes}
import play.api.mvc._
import playcms.admin._
import playcms.models.Template
import playcms.admin.services.ITemplateService
import scala.concurrent.ExecutionContext

class TemplatesController(templateService: ITemplateService)
                         (implicit val executionContext: ExecutionContext, format: Format[Template])
  extends BaseController {

  implicit val templateSeqWrites = Writes.seq[Template]

  def list = WSAction {
    templateService.getAll.map(templates => Ok(json(templates)))
  }

  def get(id: Template.ID) = WSAction {
    templateService.getById(id) map { maybeTemplate =>
      maybeTemplate.fold[SimpleResult](NotFound)(template => Ok(json(template)))
    }
  }

  def create = WSAction[Template] { request =>
    templateService.save(request.content) map (t => Created.withHeaders())
  }

  def update(id: Template.ID) = WSAction[Template] { request =>
    templateService.save(request.content) map (t => NoContent.withHeaders())
  }

  def delete(id: Template.ID) = WSAction {
    templateService.delete(id) map (_ => NoContent) recover {
      case e => BadRequest(e.toString)
    }
  }

  def uniqueCheck(id: Option[Template.ID], name: String) = WSAction {
    templateService.isUnique(id, name) map (isUnique => Ok(json(isUnique)))
  }
}

object TemplatesController extends TemplatesController(TemplateService)
