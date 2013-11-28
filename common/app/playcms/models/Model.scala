package playcms.models

trait Model[Id] {
  val id: Option[Id]
  def withId: Model[Id]
}

trait SoftDelete {
  def isDeleted: Boolean
}
