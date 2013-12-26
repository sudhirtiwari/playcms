package com.github.nrf110

import model.DustTemplate
import play.api.libs.iteratee.Enumerator
import scala.concurrent.{ExecutionContext, Future}

class Dust(implicit ec: ExecutionContext) {
  private var templateSources: Seq[ITemplateSource] = Seq.empty[ITemplateSource]
  def registerTemplateSources(sources: ITemplateSource*): Unit = {
    templateSources = sources ++ templateSources
  }

  //def registerHelpers(helpers: (String, Any)*): Unit

  def load(key: String): Future[Option[DustTemplate]] = {
    def compileOrStep(source: Option[String], remaining: List[ITemplateSource]): Future[Option[DustTemplate]] =
      source match {
        case Some(src) => DustCompiler.compile(src, key) map Option.apply
        case None => step(remaining)
      }

    def step(remaining: List[ITemplateSource]): Future[Option[DustTemplate]] =
      remaining match {
        case Nil => Future.successful(None)
        case x :: xs => for {
          maybeSrc <- x.load(key)
          template <- compileOrStep(maybeSrc, xs)
        } yield template
      }

    step(templateSources.toList)
  }
}
