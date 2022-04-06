class Parser2 {

  private val unparsedTokens = new scala.collection.mutable.Queue[Token]()

  def error(msg: String): Nothing =
    throw new Exception(msg)


  def parseTokens(tokens: List[Token]): List[AST] = {
    List()
  }

  def parse(tokens: List[Token]): Option[AST] = {
    if(tokens.isEmpty) return Option.empty[AST]
    unparsedTokens.enqueueAll(tokens)
    initialParse(Option.empty[AST])
  }


  //TODO: Identifier can be on its own as statement Ej: variable; Possible soulution -> method parseIdentifier() that first chackes currentoken.tail and the goes to parseExpression()
  private def initialParse(ast: Option[AST]): Option[AST] = {
    if(unparsedTokens.isEmpty) return ast

    unparsedTokens.front.tokenType match {
      case DECLARATION() => parseDeclaration()
      case IDENTIFIER() => Option.apply(AssignationNode((unparsedTokens.dequeue().value), parseExpression(Option.empty)))
      case PRINTLN() => Option.apply(PrintNode(parseExpression(Option.empty)))
      case _ => error(s"Expected literal, variable or 'let' but found ${unparsedTokens.dequeue().tokenType}")
    }
  }

  private def parseDeclaration(): Option[AST] = {
    checkIfUnparsedTokensEmpty("variable name")

    unparsedTokens.dequeue()
    unparsedTokens.front.tokenType match {
      case IDENTIFIER() => Option.apply(DeclarationAssignationNode(Variable(unparsedTokens.dequeue().value), parseVariableType(), parseAssignmentExpression()))
      case _ => error("Declaration must be followed by a variable")
    }
  }

  private def parseAssignmentExpression(): AST = {
    //TODO: Extact method "checkIfValidTokenAndDequeueIt()"
    unparsedTokens.dequeue()
    checkIfNextTokenIsValid(EQUAL(), "=")
    unparsedTokens.dequeue()


    parseExpression(Option.empty[AST])

  }

  //TODO: Check semicolon assertion
  private def parseExpression(expressionAST: Option[AST]): AST = {
    checkIfValidEOL()
    if(unparsedTokens.isEmpty) return expressionAST.get
    unparsedTokens.front.tokenType match {
      case LITERALNUMBER() => parseExpression(Option.apply(ConstantNumb(unparsedTokens.dequeue().value.toDouble)))
      case LITERALSTRING() => parseExpression(Option.apply(ConstantString(unparsedTokens.dequeue().value)))
      case SUM() => parseExpression(parseBinaryOperator(expressionAST, PlusBinaryOperator()))
      case SUB() => parseExpression(parseBinaryOperator(expressionAST, MinusBinaryOperator()))
      case MUL() => parseExpression(parseBinaryOperator(expressionAST, MultiplyBinaryOperator()))
      case DIV() => parseExpression(parseBinaryOperator(expressionAST, DivideBinaryOperator()))
      case SEMICOLON() => expressionAST.get
      case _ => error(s"")
    }
  }

  private def parseBinaryOperator(maybeAst: Option[AST], operator: BinaryOperator) : Option[AST]  = {
    unparsedTokens.dequeue()
    if(maybeAst.isEmpty || unparsedTokens.isEmpty) error("Expected expression")

    Option.apply(BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST])))

  }


//  private def parseSecondaryBinaryOperator(maybeAst: Option[AST], operator: BinaryOperator) : Option[AST]  = {
//    unparsedTokens.dequeue()
//    if(maybeAst.isEmpty || unparsedTokens.isEmpty) error("Expected expression")
//
//    Option.apply(BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST])))
//
//
//  }





  private def parseVariableType(): VariableTypeNode = {
    //TODO: Can use the checkIfValidTokenAndDequeueIt() methos if instead of dequeuing in Variable() we use front
    checkIfNextTokenIsValid(COLON(), ":")

    unparsedTokens.dequeue()
    unparsedTokens.front.tokenType match {
      case STRINGTYPE() => VariableTypeNode(StringVariableType())
      case NUMBERTYPE() => VariableTypeNode(NumberVariableType())
      case _ => error("Expected variable type but found " + unparsedTokens.dequeue().tokenType)
    }
  }

  private def checkIfUnparsedTokensEmpty(expected: String): Unit  =
    if (unparsedTokens.isEmpty ) error(s"Expected ${expected} but nothing was found")

  private def checkIfNextTokenIsValid(expectedType: TokenType, expectedSymbol: String): Unit =
    if (unparsedTokens.nonEmpty && unparsedTokens.front.tokenType != expectedType) error(s"Expected ${expectedSymbol} but found ${unparsedTokens.front.tokenType}")

  private def checkIfValidEOL(): Unit = {
    if (unparsedTokens.isEmpty || (unparsedTokens.tail.isEmpty && (!unparsedTokens.front.tokenType.equals(SEMICOLON())))) error("Line should end with semicolon")
//    if (unparsedTokens.tail.isEmpty &&  unparsedTokens.front.tokenType.equals(SEMICOLON())) unparsedTokens.dequeue()
  }
}
