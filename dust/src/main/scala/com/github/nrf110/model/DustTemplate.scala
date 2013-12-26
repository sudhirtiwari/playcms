package com.github.nrf110.model

import scala.concurrent.{ExecutionContext, Future}
import play.api.templates.Html
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Writes

class DustTemplate(implicit executionContext: ExecutionContext) {
  def render[A](context: A)(implicit writes: Writes[A]): Future[Html]
  def stream[A](context: A)(implicit writes: Writes[A]): Future[Enumerator[String]]
}
