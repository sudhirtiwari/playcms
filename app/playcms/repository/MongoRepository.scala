package playcms.repository

import play.api.libs.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats.toBSON
import playcms.models.Model
import reactivemongo.api.DefaultDB
import reactivemongo.bson._
import reactivemongo.core.commands.{GetLastError, Count}
import scala.concurrent.ExecutionContext

abstract class MongoRepository[T <: Model](db: DefaultDB)(implicit val ec: ExecutionContext, format: Format[T]) {
  val collectionName: String
  val collection = db.collection[JSONCollection](collectionName)

  def findById(id: String) = findOne(Json.obj("_id" -> id))
  def findAll = collection.find(BSONDocument()).cursor[T].collect[List]()
  def find(query: JsObject) = collection.find(query).cursor[T].collect[List]()
  def findOne(query: JsObject) = collection.find(query).one[T]
  def count(query: JsObject) = {
    toBSON(query) match {
      case JsSuccess(q, _) => db.command(Count(collectionName, Option(q.asInstanceOf[BSONDocument])))
      case JsError(errors) => throw new RuntimeException(errors.toString())
    }
  }

  def countAll = count(Json.obj())
  def save(entity: T) = collection.save[T](entity)
  def saveAndReload(entity: T) = {
    val entityToSave = entity.withId.asInstanceOf[T]
    for {
      _ <- save(entityToSave)
      maybeReloaded <- findById(entity.id.get)
      reloaded = maybeReloaded.get
    } yield reloaded
  }
  def update[S, U](selector: S, update: U, writeConcern: GetLastError = GetLastError(), upsert: Boolean = false, multi: Boolean = false)
                  (implicit selectionWriter: Writes[S], updateWriter: Writes[U]) =
    collection.update(selector, update, writeConcern, upsert, multi)
  def delete(id: String) = collection.remove(Json.obj("_id" -> id)) map (_ => {})
}

trait NaturalKeyMongoRepository[T <: Model] { this: MongoRepository[T] =>
  def naturalKeySelector(entity: T): JsObject
  def findByNaturalKey(entity: T) =
    findOne(naturalKeySelector(entity))
  def upsertByNaturalKey(entity: T) =
    update(naturalKeySelector(entity), entity, GetLastError(), upsert = true, multi = false)
}