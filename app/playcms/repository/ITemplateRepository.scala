package playcms.repository

import play.api.libs.json.{Format, Json}
import playcms.models.Template
import reactivemongo.api.DefaultDB
import scala.concurrent.{ExecutionContext, Future}

trait ITemplateRepository { this: MongoRepository[Template] =>
  def findById(id: String): Future[Option[Template]]
  def findAll: Future[List[Template]]
  def saveAndReload(template: Template): Future[Template]
  def findByName(name: String): Future[Option[Template]]
  def delete(id: String): Future[Unit]
}

class MongoTemplateRepository(db: DefaultDB)(override implicit val ec: ExecutionContext, format: Format[Template])
  extends MongoRepository[Template](db)
  with ITemplateRepository {

  val collectionName: String = "cms_templates"

  def findByName(name: String): Future[Option[Template]] =
    findOne(Json.obj("name" -> name))
}