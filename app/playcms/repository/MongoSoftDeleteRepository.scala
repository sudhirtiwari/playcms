package playcms.repository

import play.api.libs.json.{Format, Json, JsObject}
import playcms.models.{Model, SoftDelete}
import reactivemongo.api.DefaultDB
import scala.concurrent.{Future, ExecutionContext}

abstract class MongoSoftDeleteRepository[T <: Model with SoftDelete](db: DefaultDB)
                                                                    (override implicit val ec: ExecutionContext, format: Format[T])
  extends MongoRepository[T](db) {

  private def filterByDeleted(query: JsObject = Json.obj(), includeDeleted: Boolean = false) =
    query ++ Json.obj("isDeleted" -> includeDeleted)
  def softDelete(id: String): Future[Unit] =
    update(filterByDeleted(Json.obj("_id" -> id)), Json.obj("isDeleted" -> true)) map (_ => {})
  def findDeleted(query: JsObject) = collection.find(filterByDeleted(includeDeleted = true)).cursor[T].collect[List]()
  override def findAll = collection.find(filterByDeleted()).cursor[T].collect[List]()
  override def find(query: JsObject) = super.find(filterByDeleted(query))
  override def findOne(query: JsObject) = super.findOne(filterByDeleted(query))
  override def count(query: JsObject) = super.count(filterByDeleted(query))
  override def countAll = count(filterByDeleted())
}
