package playcms.repository

import playcms.models.{Model, SoftDelete}
import reactivemongo.bson.{BSONObjectID, BSONDocument}
import reactivemongo.api.DefaultDB
import scala.concurrent.{Future, ExecutionContext}

abstract class MongoSoftDeleteRepository[T <: Model with SoftDelete](db: DefaultDB)
                                                                    (override implicit val ec: ExecutionContext)
  extends MongoRepository[T](db) {

  private def filterByDeleted(query: BSONDocument = BSONDocument.empty, includeDeleted: Boolean = false) =
    query ++ BSONDocument("isDeleted" -> includeDeleted)
  def softDelete(id: String): Future[Unit] =
    update(filterByDeleted(BSONDocument("_id" -> new BSONObjectID(id))), BSONDocument("isDeleted" -> true)) map (_ => {})
  def findDeleted(query: BSONDocument) = collection.find(filterByDeleted(includeDeleted = true)).cursor[T].collect[List]()
  override def findAll = collection.find(filterByDeleted()).cursor[T].collect[List]()
  override def find(query: BSONDocument) = super.find(filterByDeleted(query))
  override def findOne(query: BSONDocument) = super.findOne(filterByDeleted(query))
  override def count(query: BSONDocument) = super.count(filterByDeleted(query))
  override def countAll = count(filterByDeleted())
}
