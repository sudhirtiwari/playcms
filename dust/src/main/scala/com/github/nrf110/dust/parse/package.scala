package com.github.nrf110.dust

import scala.util.parsing.input.Positional

package object parse {
  abstract class Node extends Positional
  trait Visitable extends Node {
    def nodes: List[Node]
  }
  trait Nullifyable extends Node
  trait NoOp extends Node
  trait TextNode extends NoOp {
    def text: String
  }
  case class Key(value: String) extends TextNode {
    def text = value
  }
  case class Path(value: List[String], leadingDot: Boolean = false) extends TextNode {
    def text = value.mkString(if (leadingDot) "." else "", ".", "")
  }
  case class Identifier(value: Either[Key, Path]) {
    def node = value.merge
    def text = value.fold(_.text, _.text)
  }
  case class Literal(value: String) extends TextNode {
    def text = value
  }
  abstract class Tag extends Node
  case class Reference(identifier: Identifier, filters: Filters) extends Tag with Visitable {
    def nodes = List(identifier.node, filters)
  }
  case class Comment(value: String) extends Tag with Nullifyable
  case class Special(value: String) extends Tag
  case class Partial(
    node: Node,
    context: Option[Context]) extends Tag with Visitable {
    def nodes = node :: context.toList
  }
  abstract class SectionTag extends Tag with Visitable {
    def nodes = List(identifier.node) ::: context.toList ::: List(params) ::: bodies.toList
    def identifier: Identifier
    def context: Option[Context]
    def params: Params
    def bodies: Option[Bodies]
    def withBodies(bodies: Bodies): SectionTag
  }

  object SectionTag {
    def apply(
      `type`: String,
      identifier: Identifier,
      context: Option[Context] = None,
      params: Params,
      bodies: Option[Bodies] = None): SectionTag = {

      `type` match {
        case "#" => Section(identifier, context, params, bodies)
        case "@" => ContextHelper(identifier, context, params, bodies)
        case "?" => Exists(identifier, context, params, bodies)
        case "^" => NotExists(identifier, context, params, bodies)
        case "+" => Block(identifier, context, params, bodies)
        case "<" => InlinePartial(identifier, context, params, bodies)
      }
    }
  }

  case class Section(
    identifier: Identifier,
    context: Option[Context],
    params: Params,
    bodies: Option[Bodies] = None) extends SectionTag {
    def withBodies(bodies: Bodies) = copy(bodies = Option(bodies))
  }

  case class Exists(
    identifier: Identifier,
    context: Option[Context],
    params: Params,
    bodies: Option[Bodies] = None) extends SectionTag {
    def withBodies(bodies: Bodies) = copy(bodies = Option(bodies))
  }

  case class NotExists(
    identifier: Identifier,
    context: Option[Context],
    params: Params,
    bodies: Option[Bodies] = None) extends SectionTag {
    def withBodies(bodies: Bodies) = copy(bodies = Option(bodies))
  }

  case class ContextHelper(
    identifier: Identifier,
    context: Option[Context],
    params: Params,
    bodies: Option[Bodies] = None) extends SectionTag {
    def withBodies(bodies: Bodies) = copy(bodies = Option(bodies))
  }

  case class Block(
    identifier: Identifier,
    context: Option[Context],
    params: Params,
    bodies: Option[Bodies] = None) extends SectionTag {
    def withBodies(bodies: Bodies) = copy(bodies = Option(bodies))
  }

  case class InlinePartial(
    identifier: Identifier,
    context: Option[Context],
    params: Params,
    bodies: Option[Bodies] = None) extends SectionTag {
    def withBodies(bodies: Bodies) = copy(bodies = Option(bodies))
  }

  case class Body(value: List[Node]) extends Node
  case class Bodies(params: List[Param]) extends Visitable {
    def nodes = params
    def addParam(param: Param) = copy(params = params :+ param)
  }
  object Bodies {
    val Empty = Bodies(Nil)
  }
  case class Buffer(content: String) extends TextNode {
    def text = content
  }
  case class Format(eol: String, whitespace: String) extends Nullifyable
  case class Context(identifier: Identifier) extends Visitable {
    def nodes = List(identifier.value.merge)
  }
  case class Param(name: Either[String, Literal], value: Node) extends Visitable {
    def nodes = Nil //TODO:
  }
  case class Params(values: List[Param]) extends Visitable {
    def nodes = values
  }
  object Params {
    val Emtpy = Params(Nil)
  }
  case class Filters(values: List[String]) extends NoOp

  case class ParserFailure(msg: String, line: Int, column: Int) extends RuntimeException(msg)
}
