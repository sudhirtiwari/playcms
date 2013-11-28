package playcms.models

import play.api.mvc.{AnyContent, Request, WrappedRequest}

trait RequestContext[A] extends WrappedRequest[A]

case class RequestWithContent[+A](request: Request[AnyContent], content: A)
  extends WrappedRequest[AnyContent](request)
  with RequestContext[AnyContent]
