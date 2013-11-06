package playcms.repository

import playcms.models.Model
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.DefaultDB
import reactivemongo.bson._
import reactivemongo.core.commands.{GetLastError, Count}
import scala.concurrent.ExecutionContext

abstract class MongoRepository[T <: Model](db: DefaultDB)(implicit val ec: ExecutionContext) {
  type BSONHandlerType = BSONDocumentReader[T] with BSONDocumentWriter[T] with BSONHandler[BSONDocument, T]
  implicit val bsonHandler: BSONHandlerType
  val collectionName: String
  val collection: BSONCollection = db.collection[BSONCollection](collectionName)

  def findById(id: String) = findOne(BSONDocument("_id" -> new BSONObjectID(id)))
  def findAll = collection.find(BSONDocument()).cursor[T].collect[List]()
  def find(query: BSONDocument) = collection.find(query).cursor[T].collect[List]()
  def findOne(query: BSONDocument) = collection.find(query).cursor[T].headOption
  def count(query: BSONDocument) = db.command(Count(collectionName, Option(query)))
  def countAll = count(null)
  def save(entity: T) = collection.save(entity)
  def saveAndReload(entity: T) = {
    val entityToSave = entity.withId.asInstanceOf[T]
    for {
      _ <- save(entityToSave)
      maybeReloaded <- findById(entity.id.get)
      reloaded = maybeReloaded.get
    } yield reloaded
  }
  def update[S, U](selector: S, update: U, writeConcern: GetLastError = GetLastError(), upsert: Boolean = false, multi: Boolean = false)
                  (implicit selectionWriter: BSONDocumentWriter[S], updateWriter: BSONDocumentWriter[U]) =
    collection.update(selector, update, writeConcern, upsert, multi)
  def delete(id: String) = collection.remove(BSONDocument("_id" -> new BSONObjectID(id))) map (_ => {})
}

trait NaturalKeyMongoRepository[T <: Model] { this: MongoRepository[T] =>
  def naturalKeySelector(entity: T): BSONDocument
  def findByNaturalKey(entity: T) =
    findOne(naturalKeySelector(entity))
  def upsertByNaturalKey(entity: T) =
    update(naturalKeySelector(entity), entity, GetLastError(), upsert = true, multi = false)
}