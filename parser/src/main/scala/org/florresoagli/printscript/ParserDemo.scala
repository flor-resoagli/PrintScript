//package org.florresoagli.printscript
//
//import scala.annotation.tailrec
//import scala.collection.mutable.Queue
//import scala.collection.{immutable, mutable}
//
//class ParserDemo() {
//
//  def isLeftParen(unparsedTokens: mutable.Queue[Token]): Boolean = {
//    unparsedTokens.isEmpty || unparsedTokens.front.tokenType == LEFTPARENTHESIS()
//  }
//
//  def toImmutable(queue: mutable.Queue[Token]): immutable.Queue[Token] = {
//    immutable.Queue(queue.toList: _*)
//  }
//
//  def toMutable(queue: immutable.Queue[Token]): mutable.Queue[Token] = {
//    val empty = mutable.Queue.empty[Token]
//    var tmp: immutable.Queue[Token] = queue
//    while (tmp.nonEmpty) {
//      val (token, nextQueue) = tmp.dequeue
//      empty.enqueue(token)
//      tmp = nextQueue
//    }
//    empty
//  }
//
//  def notRightParenthesis(unparsedTokens: mutable.Queue[Token]): Boolean = {
//    unparsedTokens.nonEmpty && !unparsedTokens.front.tokenType.equals(RIGHTPARENTHESIS())
//  }
//
//  def encounteredLeftParenthesis(unparsedTokens: mutable.Queue[Token]): Boolean = {
//    unparsedTokens.front.tokenType == LEFTPARENTHESIS()
//  }
//
//  private val unparsedTokens = new scala.collection.mutable.Queue[Token]()
//
//  def error(msg: String): Nothing =
//    throw new Exception(msg)
//
//  def parseTokens(tokens: List[Token]): List[AST] = {
//    if (tokens.isEmpty) return Nil
//    unparsedTokens.enqueueAll(tokens)
//    var parseTreesList = List(startParsing(unparsedTokens))
//    while (tokensAreLeft(unparsedTokens))
//      parseTreesList = parseTreesList ++ List(startParsing(unparsedTokens))
//    parseTreesList
//  }
//
//  private def tokensAreLeft(unparsedTokens: mutable.Queue[Token]): Boolean = unparsedTokens.nonEmpty
//
//  private def isSemiColon(token: Token): Boolean = token.tokenType.equals(SEMICOLON())
//
//  private def startParsing(unparsedTokens: mutable.Queue[Token]): AST = {
//    checkForSemiColon(unparsedTokens) {
//      unparsedTokens.front.tokenType match {
//        case DECLARATION() => parseTypeAndValueAssignation(unparsedTokens, parseVariableType())
//        case IDENTIFIER()  => parseIdentifier(unparsedTokens)
//        case PRINTLN()     => parsePrint(unparsedTokens)
//        case IDENTIFIER()  => parseTypeAndValueAssignation(unparsedTokens, parseConstantType())
//        case _ =>
//          error(
//            s"Expected literal, variable or 'let' but found ${unparsedTokens.dequeue().tokenType}"
//          )
//      }
//    }
//  }
//
//  private def parseIdentifier(unparsedTokens: mutable.Queue[Token]) = {
//    AssignationNode(Variable(unparsedTokens.front.value), parseAssignmentExpression())
//  }
//
//  private def parsePrint(unparsedTokens: mutable.Queue[Token]): AST = {
//    unparsedTokens.dequeue()
//    if (!isLeftParen(unparsedTokens)) error("Expected '(' after print")
//    val result = PrintNode(parseExpression(Option.empty, unparsedTokens))
//    result
//  }
//
//  private def parseTypeAndValueAssignation(
//    unparsedTokens: scala.collection.mutable.Queue[Token],
//    func: => VariableTypeNode
//  ): AST = {
//    checkIfUnparsedTokensEmpty("variable name")
//    unparsedTokens.dequeue()
//    unparsedTokens.front.tokenType match {
//      case IDENTIFIER() =>
//        DeclarationAssignationNode(
//          Variable(unparsedTokens.front.value),
//          func,
//          parseAssignmentExpression()
//        )
//      case _ => error("Declaration must be followed by a variable")
//    }
//  }
//
//  private def checkForSemiColon(unparsedTokens: mutable.Queue[Token])(func: => AST): AST = {
//    val result = func
//    unparsedTokens.headOption match {
//      case Some(token) if isSemiColon(token) =>
//        unparsedTokens.dequeue()
//        result
//      case Some(_) => error(s"Line should end with semicolon")
//      case None    => error(s"Line should end with semicolon")
//    }
//  }
//
//  private def parseAssignmentExpression(): AST = {
//    validateCurrentToken(unparsedTokens, EQUAL(), "=")
//    parseExpression(Option.empty[AST], unparsedTokens)
//
//  }
//
//  @tailrec
//  private def parseExpression(
//    expressionAST: Option[AST],
//    tokensToParse: scala.collection.mutable.Queue[Token]
//  ): AST = {
//    if (tokensToParse.isEmpty) return expressionAST.get
//    if isSemiColon(tokensToParse.front) then return expressionAST.get
//    parseExpression(acceptedExpressionsParse(expressionAST, tokensToParse), tokensToParse)
//
//  }
//
//  private def acceptedExpressionsParse(
//    expressionAST: Option[AST],
//    tokensToParse: mutable.Queue[Token]
//  ) = {
//    tokensToParse.front.tokenType match {
//      case LITERALNUMBER() => parseLiteral(tokensToParse, LITERALNUMBER())
//      case LITERALSTRING() => parseLiteral(tokensToParse, LITERALSTRING())
//      case SUM() =>
//        parseLowPriorityBinaryOperator(expressionAST, PlusBinaryOperator(), tokensToParse)
//      case SUB() =>
//        parseLowPriorityBinaryOperator(expressionAST, MinusBinaryOperator(), tokensToParse)
//      case MUL() =>
//        parseHighPriorityBinaryOperator(expressionAST, MultiplyBinaryOperator(), tokensToParse)
//      case DIV() =>
//        parseHighPriorityBinaryOperator(expressionAST, DivideBinaryOperator(), tokensToParse)
//      case IDENTIFIER()      => Option.apply(Variable(tokensToParse.dequeue().value))
//      case LEFTPARENTHESIS() => parseLeftParenthesis()
//      case _                 => error(s"")
//    }
//  }
//
//  private def parseLiteral(
//    tokensToParse: mutable.Queue[Token],
//    tokenType: TokenType
//  ): Option[AST] = {
//    if (
//      tokensToParse.tail.nonEmpty && (List(
//        IDENTIFIER(),
//        LITERALNUMBER(),
//        LITERALSTRING(),
//        LEFTPARENTHESIS()
//      ) contains tokensToParse.tail.head.tokenType)
//    ) error(s"Literal cannot be followed by: ${tokensToParse.dequeue().tokenType}")
//
//    tokenType match {
//      case LITERALNUMBER() => Option(ConstantNumb(tokensToParse.dequeue().value.toDouble))
//      case LITERALSTRING() =>
//        Option(ConstantString(tokensToParse.dequeue().value.replace("\"", "")))
//      case _ => error(s"Expected literal but found ${tokensToParse.dequeue().tokenType}")
//    }
//
//  }
//
//  private def parseLowPriorityBinaryOperator(
//    maybeAst: Option[AST],
//    operator: BinaryOperator,
//    tokensToParse: scala.collection.mutable.Queue[Token]
//  ): Option[AST] = {
//    tokensToParse.dequeue()
//    if (maybeAst.isEmpty || tokensToParse.isEmpty) error("Expected expression")
//
//    Option.apply(
//      BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], tokensToParse))
//    )
//
//  }
//
//  private def parseLeftParenthesis(): Option[AST] = {
//    unparsedTokens.dequeue()
//    if (unparsedTokens.isEmpty) error("Expected expression")
//
//    val newQueue = scala.collection.mutable.Queue[Token]()
//    //    if(!unparsedTokens.contains(_: lexer.Token)(_ == lexer.RIGHTPARENTHESIS())) error("Expected ')'")
//
//    var buildingAST = Option.empty[AST]
//    while (notRightParenthesis(unparsedTokens)) {
//      if (isLeftParen(unparsedTokens)) buildingAST = parseLeftParenthesis()
//      newQueue.enqueue(unparsedTokens.dequeue())
//    }
//
//    unparsedTokens.dequeue()
//    Option.apply(parseExpression(buildingAST, newQueue))
//
//  }
//
//  private def parseHighPriorityBinaryOperator(
//    maybeAst: Option[AST],
//    operator: BinaryOperator,
//    tokensToParse: scala.collection.mutable.Queue[Token]
//  ): Option[AST] = {
//    tokensToParse.dequeue()
//    if (maybeAst.isEmpty || tokensToParse.isEmpty) error("Expected expression")
//
//    if (isLeftParen(tokensToParse))
//      return Option.apply(
//        BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], tokensToParse))
//      )
//
//    val newQueue = scala.collection.mutable.Queue[Token]()
//    newQueue.enqueue(tokensToParse.dequeue())
//    Option.apply(
//      BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], newQueue))
//    )
//
//  }
//
//  private def parseVariableType(): VariableTypeNode = {
//    validateCurrentToken(unparsedTokens, COLON(), ":")
//
//    unparsedTokens.front.tokenType match {
//      case STRINGTYPE() => VariableTypeNode(StringVariableType())
//      case NUMBERTYPE() => VariableTypeNode(NumberVariableType())
//      case _ => error("Expected variable type but found " + unparsedTokens.dequeue().tokenType)
//    }
//  }
//  private def parseConstantType(): VariableTypeNode = {
//    validateCurrentToken(unparsedTokens, COLON(), ":")
//
//    unparsedTokens.front.tokenType match {
//      case STRINGTYPE() => VariableTypeNode(ConstantStringType())
//      case NUMBERTYPE() => VariableTypeNode(ConstantNumberType())
//      case _ => error("Expected variable type but found " + unparsedTokens.dequeue().tokenType)
//    }
//  }
//
//  /*TODO: Method shouldnt mutate list. Return new one*/
//  private def validateCurrentToken(
//    tokens: scala.collection.mutable.Queue[Token],
//    expectedTokenType: TokenType,
//    symbol: String
//  ) = {
//    tokens.dequeue()
//    if (tokens.nonEmpty && tokens.front.tokenType != expectedTokenType)
//      error(s"Expected $symbol but found ${tokens.front.tokenType}")
//    tokens.dequeue()
//  }
//
//  private def checkIfUnparsedTokensEmpty(expected: String): Unit =
//    if (unparsedTokens.isEmpty) error(s"Expected $expected but nothing was found")
//
//}
