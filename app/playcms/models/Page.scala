package playcms.models

case class Page(
  id: Option[String],
  site: Site,
  path: String,
  template: Template,
  contentAreas: Map[String, String]
)
