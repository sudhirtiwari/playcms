package playcms.repository

import play.api.libs.json.{Writes, Format, Json}
import playcms.models.Template
import reactivemongo.api.DefaultDB
import scala.concurrent.{ExecutionContext, Future}

trait ITemplateRepository { this: MongoRepository[Template, Template.ID] =>
  def findById(id: Template.ID): Future[Option[Template]]
  def findAll: Future[List[Template]]
  def saveAndReload(template: Template): Future[Template]
  def findByName(name: String): Future[Option[Template]]
  def delete(id: Template.ID): Future[Unit]
}

class MongoTemplateRepository(db: DefaultDB)
                             (implicit ec: ExecutionContext, format: Format[Template], idWrites: Writes[Template.ID])
  extends MongoRepository[Template, Template.ID](db)(ec, format, idWrites)
  with ITemplateRepository {

  val collectionName: String = "cms_templates"

  def findByName(name: String): Future[Option[Template]] = findOne(Json.obj("name" -> name))
}