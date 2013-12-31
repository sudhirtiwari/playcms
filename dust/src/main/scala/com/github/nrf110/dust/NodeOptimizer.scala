package com.github.nrf110.dust

import model.Context
import parse.{Node, NoOp, Nullifyable, Visitable, Special, Buffer, Body}
import scala.annotation.tailrec

object FilterNode {
  def apply[A <: Node](node: A)(implicit ctx: Context): Option[NodeBranch] =
    (node match {
      case _: Nullifyable => new NullifyOptimizer[A]
      case _: NoOp => new NoOptOptimizer[A]()
      case _: Visitable => new VisitOptimizer[A]
      case _: Special => new ConvertSpecialOptimizer[A]
      case _: Body => new CompactBuffersOptimizer[A]
    })(node)
}

trait NodeOptimizer[A <: Node] {
  def apply(node: A)(implicit ctx: Context): Option[NodeBranch]
}

class NoOptOptimizer[A <: NoOp] extends NodeOptimizer[A] {
  def apply(node: A)(implicit ctx: Context) = Option(NodeBranch(node))
}

class NullifyOptimizer[A <: Nullifyable] extends NodeOptimizer[A] {
  def apply(node: A)(implicit ctx: Context) = None
}

class VisitOptimizer[A <: Visitable] extends NodeOptimizer[A] {
  def apply(node: A)(implicit ctx: Context) =
    Option(NodeBranch(
      node,
      node.nodes flatMap (FilterNode(_).toList)))
}

class ConvertSpecialOptimizer[A <: Special] extends NodeOptimizer[A] {
  import ConvertSpecialOptimizer._
  def apply(node: A)(implicit ctx: Context) = specialCharacters.get(node.value) map (char => NodeBranch(Buffer(char)))
}

object ConvertSpecialOptimizer {
  val specialCharacters = Map(
    "s" -> " ",
    "n" -> "\n",
    "r" -> "\r",
    "lb" -> "{",
    "rb" -> "}"
  )
}

class CompactBuffersOptimizer[A <: Body] extends NodeOptimizer[A] {
  def apply(node: A)(implicit ctx: Context) = {
    def compact(buffer: Buffer, remaining: List[Node], memo: Option[Buffer], accum: List[NodeBranch]): List[NodeBranch] =
      memo match {
        case Some(Buffer(content)) => step(remaining, Some(Buffer(buffer.content + content)), accum)
        case None => step(remaining, Some(buffer), accum)
      }

    @tailrec
    def step(nodes: List[Node], memo: Option[Buffer], accum: List[NodeBranch]): List[NodeBranch] =
      nodes match {
        case x :: xs => FilterNode(x) match {
          case Some(NodeBranch(buffer: Buffer, _)) => compact(buffer, xs, memo, accum)
          case Some(branch) => step(xs, None, accum ::: memo.map(NodeBranch(_)).toList ::: List(branch) )
          case None => step(xs, None, accum)
        }
        case Nil => accum
      }

    Option(NodeBranch(
      node,
      step(node.value, None, Nil)))
  }
}