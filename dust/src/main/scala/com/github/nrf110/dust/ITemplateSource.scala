package com.github.nrf110.dust

import util._
import java.io.File
import play.api.libs.iteratee.{Iteratee, Enumerator}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait ITemplateSource {
  def getPrefix: String = ""
  def setPrefix: String = ""
  def load(key: String)(implicit executionContext: ExecutionContext): Future[Option[String]]
}

class FileSource extends ITemplateSource {
  def load(key: String)(implicit executionContext: ExecutionContext) =
    Try(new File(key)).fold(
      onSuccess = { f =>
        for {
          iteratee <- Enumerator.fromFile(f).apply(Iteratee.consume[Array[Byte]]())
          bytes <- iteratee.run
          s = new String(bytes)
        } yield Option(s)
      },
      onFailure = { t => Future.successful(None) })
}