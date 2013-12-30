//package com.github.nrf110
//
//import model.Context
//import parse.Body
//import play.api.libs.json.Json
//
//object CompileParts {
//  def apply(body: Body)(implicit ctx: Context) =
//    body.value map (NodeCompiler(_))
//}
//
//trait NodeCompiler[A <: parse.Node] {
//  def apply(node: A, children: List[NodeBranch])(implicit ctx: Context)
//}
//
//object NodeCompiler {
//  def apply(node: NodeBranch) =
//    node.head match {
//      case body: parse.Body =>
//      case buffer: parse.Buffer =>
//      case format: parse.Format =>
//      case reference: parse.Reference =>
//      case sectionTag: parse.SectionTag =>
//      case partial: parse.Partial =>
//      case context: parse.Context =>
//      case params: parse.Params =>
//      case bodies: parse.Bodies =>
//      case param: parse.Param =>
//      case filters: parse.Filters =>
//      case key: parse.Key =>
//      case path: parse.Path =>
//      case literal: parse.Literal =>
//    }
//}
//
//object BodyCompiler extends NodeCompiler[parse.Body] {
//  def apply(node: parse.Body, children: List[NodeBranch])(implicit ctx: Context): Unit = ???
//}
//
//object BufferCompiler extends NodeCompiler[parse.Buffer] {
//  def apply(node: parse.Buffer, children: List[NodeBranch])(implicit ctx: Context) = node.content
//}
//
//object FormatCompiler extends NodeCompiler[parse.Format] {
//  def apply(node: parse.Format, children: List[NodeBranch])(implicit ctx: Context) = ???
//}
//
//
