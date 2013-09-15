package playcms.repository

import playcms.models.Domain
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.DefaultDB
import reactivemongo.bson._
import reactivemongo.core.commands.{GetLastError, Count}
import scala.concurrent.{Future, ExecutionContext}

abstract class MongoRepository[T <: Domain](db: DefaultDB) {
  type BSONHandlerType = BSONDocumentReader[T] with BSONDocumentWriter[T] with BSONHandler[BSONDocument, T]
  implicit val bsonHandler: BSONHandlerType
  val collectionName: String
  val collection: BSONCollection = db.collection[BSONCollection](collectionName)

  def findById(id: String)(implicit ec: ExecutionContext) =
    findOne(BSONDocument("_id" -> new BSONObjectID(id)))

  def findAll(implicit ec: ExecutionContext) =
    collection.find(BSONDocument()).cursor[T].toList()

  def find(query: BSONDocument)(implicit ec: ExecutionContext) =
    collection.find(query).cursor[T].enumerate()

  def findOne(query: BSONDocument)(implicit ec: ExecutionContext) =
    collection.find(query).cursor[T].headOption()

  def count(query: BSONDocument)(implicit ec: ExecutionContext) =
    db.command(Count(collectionName, Option(query)))

  def countAll(implicit ec: ExecutionContext) =
    count(null)

  def save(entity: T)(implicit ec: ExecutionContext) =
    collection.save(entity)

  def saveAndReload(entity: T)(implicit ec: ExecutionContext) = {
    val entityToSave = entity.withId.asInstanceOf[T]
    for {
      _ <- save(entityToSave)
      maybeReloaded <- findById(entity.id.get)
      reloaded = maybeReloaded.get
    } yield reloaded
  }

  def update[S, U](selector: S, update: U, writeConcern: GetLastError = GetLastError(), upsert: Boolean = false, multi: Boolean = false)
                  (implicit selectionWriter: BSONDocumentWriter[S], updateWriter: BSONDocumentWriter[U], ec: ExecutionContext) =
    collection.update(selector, update, writeConcern, upsert, multi)

  def delete(id: String)(implicit ec: ExecutionContext): Future[Unit] =
    collection.remove(BSONDocument("_id" -> new BSONObjectID(id))) map (_ => Unit)
}

trait NaturalKeyMongoRepository[T <: Domain] { this: MongoRepository[T] =>
  def naturalKeySelector(entity: T)(implicit ec: ExecutionContext): BSONDocument
  def findByNaturalKey(entity: T)(implicit ec: ExecutionContext) =
    findOne(naturalKeySelector(entity))
  def upsertByNaturalKey(entity: T)(implicit ec: ExecutionContext) =
    update(naturalKeySelector(entity), entity, GetLastError(), true, false)
}