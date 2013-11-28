package playcms.util

object UrlHelper {
  def url(root: String, parts: String*) =
    parts.foldLeft(root) { (url, part) =>
      url.reverse.dropWhile(_ == '/').reverse + '/' + part.dropWhile(_ == '/')
    }
}
