package playcms.models

trait Model {
  val id: Option[String]
  def withId: Model
}

trait SoftDelete {
  def isDeleted: Boolean
}
