package com.github.nrf110.parse

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
    sectionStartTag <~ "/" ~ rightDelim ^^ {
      case t => t.withBodies(Bodies.Empty)
    }

  def sectionStartTag = leftDelim ~> "[#?^<+@%]".r ~ identifier ~ context ~ params ^^ {
    case t ~ id ~ ctx ~ p => SectionTag(t, id, ctx, p)
  }
  def endTag = leftDelim ~ "/" ~> identifier <~ rightDelim
  def context = opt(":" ~> identifier) ^^ { case id => id map Context }
  def params = withWhitespace(rep(ws ~> key ~ "=" ~ (identifier ^^ { case id => id.node } | inline) ^^ {
    case k ~ "=" ~ v => Param(Right(Literal(k)), v)
  })) ^^ { case p => Params(p) }

  def bodies = rep((leftDelim ~ ":" ~> key <~ rightDelim) ~ body ^^ {
    case name ~ value => Param(Right(Literal(name)), value)
  }) ^^ { case p => Bodies(p) }

  def reference = leftDelim ~> identifier ~ filters <~ rightDelim ^^ { case id ~ f => Reference(id, f) }

  def partial = leftDelim ~ ">" ~> (key ^^ Literal | inline) ~ context <~ "/" ~ rightDelim ^^ {
    case k ~ ctx => Partial(k, ctx)
  }

  def filters = rep("|" ~> key) ^^ Filters
  def special = leftDelim ~ "~" ~> key <~ rightDelim ^^ Special
  def identifier = (path ^^ { case p => Right(p) } | key ^^ { case k => Left(Key(k)) }) ^^ Identifier
  def path = opt(key) ~ rep1("\\." ~> key) ^^ { case head ~ tail => Path(head.toList ::: tail, head.isDefined) } |
    "." ^^ { case _ => Path(Nil, true) }
  def key = ident

  def inline =
    "\"" ~ "\"" ^^ { _ => Literal("") } |
    "\"" ~> `literal` <~ "\"" ^^ Literal |
    "\"" ~> rep1(inlinePart) <~ "\"" ^^ Body

  def inlinePart =
    special | reference | `literal` ^^ { case lit => Buffer(lit) }

  def buffer =
    withWhitespace(eol ~ rep(ws)) ^^ { case lineEnd ~ whitespace => Format(lineEnd, whitespace.mkString("")) } |
    rep1(not(tag) ~ not(eol) ~ not(comment) ~> ".") ^^ { case chars => Buffer(chars.mkString("")) }

  def `literal` = rep1(not(tag) ~ not(eol) ~> esc | "[^\"]".r) ^^ (_.mkString(""))
  def esc = "\\\"".r
  def comment = "{!" ~> rep(not("!}") ~> ".") <~ "!}" ^^ { case chars => Comment(chars.mkString("")) }
  def tag =
    (leftDelim ~ "[#?^><+%:@/~%]".r ~ rep1(not(rightDelim | eol) ~> ".") ~ rightDelim) |
    reference

  def leftDelim = "{"
  def rightDelim = "}"
  def eol = "\n" | "\r\n" | "\r" | "\u2028" | "\u2029"
  def ws = "[\t\\v\f \u00A0\uFEFF]"

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
