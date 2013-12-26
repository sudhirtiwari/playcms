package com.github.nrf110

import model.DustTemplate
import parse._
import scala.concurrent.Future

object DustCompiler {
  def compile(src: String, name: String): Future[DustTemplate] = {
    val parser = new DustParser
    parser.parseAll(parser.body, src) match {
      case parser.Success(ast: Body, _) => compile(ast, name)
      case parser.Failure(msg, next) => next.pos
    }
  }

  def compile(body: Body, name: String): Future[DustTemplate]
}

case class NodeBranch(head: Node, children: List[NodeBranch] = Nil)