package playcms.repository

import playcms.models.Template
import concurrent.{ExecutionContext, Future}
import reactivemongo.api.DefaultDB
import reactivemongo.bson.BSONDocument

trait ITemplateRepository { this: MongoRepository[Template] =>
  def findById(id: String): Future[Option[Template]]
  def findAll: Future[List[Template]]
  def saveAndReload(template: Template): Future[Template]
  def findByName(name: String): Future[Option[Template]]
  def delete(id: String): Future[Unit]
}

class MongoTemplateRepository(db: DefaultDB)(override implicit val ec: ExecutionContext)
  extends MongoRepository[Template](db)
  with ITemplateRepository {

  val collectionName: String = "cms_templates"
  implicit val bsonHandler = Template.TemplateBSONHandler

  def findByName(name: String): Future[Option[Template]] =
    findOne(BSONDocument("name" -> name))
}