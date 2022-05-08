package org.florresoagli.printscript
import  org.florresoagli.printscript.Token

import scala.collection.immutable
import scala.collection.immutable.Queue

class Parser(validInitialTokens: List[TokenType], parserProvider: ParserProvider) {
  type ParsingResult = (AST, Queue[Token])



  def parseTokens(tokens: List[Token]): List[AST] = {
    if (tokens.isEmpty) return Nil
    val tokensQueue = Queue[Token](tokens: _*)
    var (firstAST, newTokens) = startParsingLine(tokensQueue)
    var astList = List(firstAST)
    var auxQueue = newTokens
    while (tokensAreLeft(auxQueue)) {
      val (ast, tokensLeft) = startParsingLine(auxQueue)
      astList = astList :+ ast
      auxQueue = tokensLeft
    }
//    if (tokensAreLeft(result._2)) error("Unexpected token: " + result._2.head)
    astList
  }

  private def startParsingLine(unparsedTokens: Queue[Token]): (AST, Queue[Token]) = {
    var result: ParsingResult = (EmptyNode(), unparsedTokens)
    val (ast, newQueue) = getParserResult(unparsedTokens)

    val queue = checkForTermianlLeftUnparsed(newQueue)
    if (ast.isEmpty()) error(s"Expected literal, variable or 'let' but found ${unparsedTokens.head.tokenType}")
    (ast, queue)
  }


   def getParserResult(unparsedTokens: Queue[Token]): ParsingResult = {
    parserProvider
      .getParsers(validInitialTokens, IdentifierState.Reassignation)
      .find(parser => parser.isValid(unparsedTokens))
      .getOrElse(error(s"Expected ${validInitialTokens.mkString(", ")} but found ${unparsedTokens.head.tokenType}"))
      .parse(unparsedTokens, EmptyNode())
  }

  private def checkForTermianlLeftUnparsed(queue: Queue[Token]): Queue[Token] = {
    if (queue.isEmpty) error(("Line should end with semicolon"))
    if (queue.head.tokenType == SEMICOLON() || queue.head.tokenType == RIGHTBRACE()) queue.tail
    else queue
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


}
