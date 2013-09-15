package playcms.models

trait Domain {
  val id: Option[String]
  def withId: Domain
}
