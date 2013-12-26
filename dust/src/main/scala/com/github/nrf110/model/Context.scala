package com.github.nrf110.model

import play.api.libs.json._
import scala.collection.immutable.Stack
import play.api.libs.json.JsObject
import scala.Some

case class Context(
  stack: Stack[JsValue],
  global: JsObject = Json.obj(),
  blocks: Seq[_] = Nil) {

  def get(key: String): JsValue = {
    def step(stack: Stack[JsValue]): Option[JsValue] =
      stack.headOption match {
        case Some(head: JsObject) => (head \ key).asOpt[JsValue] orElse step(stack.tail)
        case Some(_) => step(stack.tail)
        case None => None
      }

    step(stack) orElse (global \ key).asOpt[JsValue] getOrElse JsUndefined(s"Unable to find $key")
  }

  def getPath(current: Boolean, keys: List[String]): JsValue = {
    def step(remaining: List[String], ctx: JsValue): Option[JsValue] =
      remaining match {
        case x :: xs => (ctx \ x).asOpt[JsValue] flatMap (step(xs, _))
        case Nil => Option(ctx)
      }

    if (current && keys.isEmpty) stack.head
    else step(keys, stack.head) getOrElse JsUndefined(s"Unable to find ${keys.mkString(".")}")
  }

  def current = stack.headOption

  def push(head: JsValue) = copy(stack = stack.push(head))

  def rebase(head: JsValue) = copy(stack = Stack(head))
}

object Context {
  def apply(context: Context): Context = context
  def apply[A](context: A)(implicit writes: Writes[A]): Context =
    Context(Stack(
      writes.writes(context)))
}
