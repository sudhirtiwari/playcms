package playcms.repository

import play.api.libs.json.{Writes, Format, Json, JsObject}
import playcms.models.{Model, SoftDelete}
import reactivemongo.api.DefaultDB
import scala.concurrent.{Future, ExecutionContext}

abstract class MongoSoftDeleteRepository[T <: Model[ID] with SoftDelete, ID](db: DefaultDB)
                                                                            (implicit override val ec: ExecutionContext,
                                                                             override val format: Format[T],
                                                                             override val idWrites: Writes[ID])
  extends MongoRepository[T, ID](db) {

  private def filterByDeleted(query: JsObject = Json.obj(), includeDeleted: Boolean = false) =
    query ++ Json.obj("isDeleted" -> includeDeleted)
  def softDelete(id: ID): Future[Unit] =
    update(filterByDeleted(Json.obj("_id" -> id)), Json.obj("isDeleted" -> true)) map (_ => {})
  def findDeleted(query: JsObject) = collection.find(filterByDeleted(includeDeleted = true)).cursor[T].collect[List]()
  override def findAll = collection.find(filterByDeleted()).cursor[T].collect[List]()
  override def find(query: JsObject) = super.find(filterByDeleted(query))
  override def findOne(query: JsObject) = super.findOne(filterByDeleted(query))
  override def count(query: JsObject) = super.count(filterByDeleted(query))
  override def countAll = count(filterByDeleted())
}
