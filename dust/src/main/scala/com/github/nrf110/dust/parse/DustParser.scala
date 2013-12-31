package com.github.nrf110.dust.parse

import scala.util.parsing.combinator._

class DustParser extends JavaTokenParsers {
  def body: Parser[Body] = rep(part) ^^ Body
  def part = comment | section | partial | special | reference | buffer

  def section =
    sectionStartTag ~ rightDelim ~ body ~ bodies ~ endTag ^? {
      case t ~ "}" ~ b ~ e ~ end if t.identifier.text == end.text =>
        t.withBodies{
          e.addParam(Param(Right(Literal("block")), b))
        }
    } |
    sectionStartTag <~ "/".r ~ rightDelim ^^ {
      case t => t.withBodies(Bodies.Empty)
    }

  def sectionStartTag = leftDelim ~> "[#?^<+@%]".r ~ identifier ~ context ~ params ^^ {
    case t ~ id ~ ctx ~ p => SectionTag(t, id, ctx, p)
  }
  def endTag = leftDelim ~ "/".r ~> identifier <~ rightDelim
  def context = opt(":".r ~> identifier) ^^ { case id => id map Context }
  def params = withWhitespace(rep(ws ~> key ~ "=".r ~ (identifier ^^ { case id => id.node } | inline) ^^ {
    case k ~ "=" ~ v => Param(Right(Literal(k)), v)
  })) ^^ { case p => Params(p) }

  def bodies = rep((leftDelim ~ ":".r ~> key <~ rightDelim) ~ body ^^ {
    case name ~ value => Param(Right(Literal(name)), value)
  }) ^^ { case p => Bodies(p) }

  def reference = leftDelim ~> identifier ~ filters <~ rightDelim ^^ { case id ~ f => Reference(id, f) }

  def partial = leftDelim ~ ">".r ~> (key ^^ Literal | inline) ~ context <~ "/".r ~ rightDelim ^^ {
    case k ~ ctx => Partial(k, ctx)
  }

  def filters = rep("|".r ~> key) ^^ Filters
  def special = leftDelim ~ "~".r ~> key <~ rightDelim ^^ Special
  def identifier = (path ^^ { case p => Right(p) } | key ^^ { case k => Left(Key(k)) }) ^^ Identifier
  def path = opt(key) ~ rep1("\\.".r ~> key) ^^ { case head ~ tail => Path(head.toList ::: tail, leadingDot = head.isDefined) } |
    ".".r ^^ { case _ => Path(Nil, leadingDot = true) }
  def key = ident

  def inline =
    "\"".r ~ "\"".r ^^ { _ => Literal("") } |
    "\"".r ~> `literal` <~ "\"".r ^^ Literal |
    "\"".r ~> rep1(inlinePart) <~ "\"".r ^^ Body

  def inlinePart =
    special | reference | `literal` ^^ { case lit => Buffer(lit) }

  def buffer =
    withWhitespace(eol ~ rep(ws)) ^^ { case lineEnd ~ whitespace => Format(lineEnd, whitespace.mkString("")) } |
    rep1(not(tag) ~ not(eol) ~ not(comment) ~> ".".r) ^^ { case chars => Buffer(chars.mkString("")) }

  def `literal` = rep1(not(tag) ~ not(eol) ~> esc | "[^\"]".r) ^^ (_.mkString(""))
  def esc = "\\\"".r
  def comment = "{!".r ~> rep(not("!}".r) ~> ".".r) <~ "!}".r ^^ { case chars => Comment(chars.mkString("")) }
  def tag =
    (leftDelim ~ "[#?^><+%:@/~%]".r ~ rep1(not(rightDelim | eol) ~> ".".r) ~ rightDelim) |
    reference

  def leftDelim = "{".r
  def rightDelim = "}".r
  def eol = "\n".r | "\r\n".r | "\r".r | "\u2028".r | "\u2029".r
  def ws = "[\t\\v\f \u00A0\uFEFF]".r

  protected var _skipWhitespace = whiteSpace.toString().length > 0
  override def skipWhitespace = _skipWhitespace
  protected var customWhitespace = whiteSpace
  //TODO: make this thread-safe
  private def withWhitespace[T](p: => Parser[T]): Parser[T] = Parser[T] { in =>
    val originalVal = _skipWhitespace
    _skipWhitespace = false
    val result = p(in)
    _skipWhitespace = originalVal
    result
  }
}
