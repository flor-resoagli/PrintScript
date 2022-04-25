import scala.::

trait ParserBuilder {
  def build(): Parser
}


class Pareser10 extends ParserBuilder {

  def build(): Parser = {
    val startOfLineParsers = List[TokenType](
        DECLARATION(), IDENTIFIER(), PRINTLN()
    )
    new Parser(startOfLineParsers, ParserProvider10())
  }
}

class Pareser11 extends ParserBuilder {

  def build(): Parser = {
    val startOfLineParsers = List[TokenType](
      DECLARATION(), IDENTIFIER(), PRINTLN(), CONSTANT(), IF()
    )
    new Parser(startOfLineParsers, ParserProvider11())
  }
}