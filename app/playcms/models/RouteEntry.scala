package playcms.models

case class RouteEntry(
  fqdn: String,
  path: String,
  pageId: Option[String],
  status: Int,
  redirectTo: Option[String])
