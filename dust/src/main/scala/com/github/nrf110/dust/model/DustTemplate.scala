package com.github.nrf110.dust.model

import play.api.templates.Html
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Writes
import scala.concurrent.{ExecutionContext, Future}

//TODO: implement this
class DustTemplate()(implicit executionContext: ExecutionContext) {
  def render[A](context: A)(implicit writes: Writes[A]): Future[Html] = ???
  def stream[A](context: A)(implicit writes: Writes[A]): Future[Enumerator[String]] = ???
}
