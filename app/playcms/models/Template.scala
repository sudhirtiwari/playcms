package playcms.models

case class Template(
  id: Option[String],
  name: String,
  templateText: String,
  contentType: Option[String] = Some("text/html")
)
