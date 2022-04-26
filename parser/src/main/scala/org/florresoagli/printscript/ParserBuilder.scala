package org.florresoagli.printscript

import org.florresoagli.printscript.Parser

import scala.::

trait ParserBuilder {
  def build(): Parser
}

class Parser10 extends ParserBuilder {

  def build(): Parser = {
    val startOfLineParsers = List[TokenType](DECLARATION(), IDENTIFIER(), PRINTLN())
    new Parser(startOfLineParsers, ParserProvider10())
  }
}

class Parser11 extends ParserBuilder {

  def build(): Parser = {
    val startOfLineParsers =
      List[TokenType](DECLARATION(), IDENTIFIER(), PRINTLN(), CONSTANT(), IF(), READINPUT())
    new Parser(startOfLineParsers, ParserProvider11())
  }
}
