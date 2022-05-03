package org.florresoagli.printscript
import  org.florresoagli.printscript.Token

import scala.collection.immutable
import scala.collection.immutable.Queue

class Parser(validInitialTokens: List[TokenType], parserProvider: ParserProvider) {
  type ParsingResult = (AST, Queue[Token])

  private def startParsingLine(unparsedTokens: Queue[Token]): (AST, Queue[Token]) = {
    var result: ParsingResult = (EmptyNode(), unparsedTokens)
    // TODO: extract method here
    result = parserProvider
            .getParsers(validInitialTokens, IdentifierState.Reassignation)
            .find(parser => parser.isValid(unparsedTokens))
            .get
            .parse(unparsedTokens, EmptyNode())


    val queue = checkForTermianlLeftUnparsed(result)
    if (result._1.isEmpty()) error(s"Expected literal, variable or 'let' but found ${unparsedTokens.head.tokenType}")
    (result._1, queue)
  }

  private def checkForTermianlLeftUnparsed(result: (AST, Queue[Token])): Queue[Token] = {
    if (result._2.isEmpty) error(("Line should end with semicolon"))
    if (result._2.head.tokenType == SEMICOLON() || result._2.head.tokenType == RIGHTBRACE()) result._2.tail
    else result._2
  }





  def error(msg: String): Nothing = {
    throw new Exception(msg)
  }
  private def tokensAreLeft(unparsedTokens: Queue[Token]): Boolean = unparsedTokens.nonEmpty
  private def isSemiColon(token: Token): Boolean = token.tokenType.equals(SEMICOLON())
  def checkForSemiColon(parsingResult: ParsingResult): ParsingResult = {
    parsingResult._2.headOption match {
      case Some(token) if isSemiColon(token) =>
        val newTokens = parsingResult._2.dequeue._2
        (parsingResult._1, newTokens)
      case Some(_) => error(s"Line should end with semicolon")
      case None    => error(s"Line should end with semicolon")
    }
  }

  def parseTokens(tokens: List[Token]): List[AST] = {
    if (tokens.isEmpty) return Nil
    val tokensQueue = Queue[Token](tokens: _*)
    var (firstAST, newTokens) = startParsingLine(tokensQueue)
    var result = (List(firstAST), newTokens)
    // TODO: extract method for result ++ ...
    while (tokensAreLeft(result._2)) {
      val (ast, tokensLeft) = startParsingLine(result._2)
      result = (result._1 ++ List(ast), tokensLeft)
    }
    result._1
  }
}
