package com.github.nrf110.dust.model

import scala.concurrent.Future

trait StreamLike {
  def head: Future[Chunk]
  def withHead(head: Chunk): Future[StreamLike]
  def flush: Future[StreamLike]
}
