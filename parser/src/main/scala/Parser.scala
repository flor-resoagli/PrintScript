class Parser {

  private val unparsedTokens = new scala.collection.mutable.Queue[Token]()
  private var previous: Option[Token] = None

  def error(msg: String): Nothing =
    throw new Exception(msg)


  def parseTokens(tokens: List[Token]): List[AST] = {
    if(tokens.isEmpty) return List().empty
    unparsedTokens.enqueueAll(tokens)
    val parseTreesList = initialParse(Option.empty[AST]).toList
    if(unparsedTokens.isEmpty && previous.nonEmpty && previous.get.tokenType.equals(SEMICOLON())) parseTreesList
    else if (unparsedTokens.isEmpty && (previous.isEmpty || previous.get.tokenType.!=(SEMICOLON()))) error("Line should end with semicolon")
    else parseTreesList ++ initialParse(Option.empty[AST]).toList

  }


  private def initialParse(ast: Option[AST]): Option[AST] = {
    if(previous.nonEmpty && previous.get.tokenType.!=(SEMICOLON())) error("Line should end with semicolon")

    unparsedTokens.front.tokenType match {
      case DECLARATION() => parseDeclaration()
      case IDENTIFIER() => Option.apply(AssignationNode(Variable((unparsedTokens.front.value)), parseAssignmentExpression()))
//      case PRINTLN() => Option.apply(PrintNode(parseExpression(Option.empty, unparsedTokens)))
      case _ => error(s"Expected literal, variable or 'let' but found ${unparsedTokens.dequeue().tokenType}")
    }
  }


  private def parseDeclaration(): Option[AST] = {
    previous = Option.apply(unparsedTokens.front)
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


    parseExpression(Option.empty[AST], unparsedTokens)

  }

  /*TODO:
     1. Instead of mutable colection, next call to methos could pass "tail" as parameter
   */
  private def parseExpression(expressionAST: Option[AST], unparsedTokens: scala.collection.mutable.Queue[Token]): AST = {

    if(unparsedTokens.isEmpty) return expressionAST.get
    unparsedTokens.front.tokenType match {
      case LITERALNUMBER() => parseExpression(Option.apply(ConstantNumb(unparsedTokens.dequeue().value.toDouble)), unparsedTokens)
      case LITERALSTRING() => parseExpression(Option.apply(ConstantString(unparsedTokens.dequeue().value.replace("\"", ""))), unparsedTokens)
      case SUM() => parseExpression(parseBinaryOperator(expressionAST, PlusBinaryOperator()), unparsedTokens)
      case SUB() => parseExpression(parseBinaryOperator(expressionAST, MinusBinaryOperator()), unparsedTokens)
      case MUL() => parseExpression(parseSecondaryBinaryOperator(expressionAST, MultiplyBinaryOperator()), unparsedTokens)
      case DIV() => parseExpression(parseSecondaryBinaryOperator(expressionAST, DivideBinaryOperator()), unparsedTokens)
      case IDENTIFIER() => parseExpression(Option.apply(Variable(unparsedTokens.dequeue().value)), unparsedTokens)
      case SEMICOLON() => parseSemicolon(expressionAST)
      case _ => error(s"")
    }
  }

//  private def parseLiteral(): Unit = {
//    unparsedTokens.dequeue()
//    unparsedTokens.front.tokenType match {
//      case LITERALNUMBER() => parseExpression(Option.apply(ConstantNumb(unparsedTokens.dequeue().value.toDouble)), unparsedTokens)
//      case LITERALSTRING() => parseExpression(Option.apply(ConstantString(unparsedTokens.dequeue().value.replace("\"", ""))), unparsedTokens)
//      case _ => error(s"")
//    }
//  }
//
//  private def parseLowPriorityOperation(): Unit ={
//    unparsedTokens.dequeue()
//    unparsedTokens.front.tokenType match {
//      case MUL() => parseExpression(parseSecondaryBinaryOperator(expressionAST, MultiplyBinaryOperator()), unparsedTokens)
//      case DIV() => parseExpression(parseSecondaryBinaryOperator(expressionAST, DivideBinaryOperator()), unparsedTokens)
//      case _ => error(s"")
//    }
//  }



  private def parseSemicolon(expressionAST: Option[AST]): AST ={
    if(previous.nonEmpty && previous.get.tokenType.equals(SEMICOLON())) error("Consecutive semicolons not allowes")
    previous = Option.apply(unparsedTokens.dequeue())
    expressionAST.get
  }

  private def parseBinaryOperator(maybeAst: Option[AST], operator: BinaryOperator) : Option[AST]  = {
    unparsedTokens.dequeue()
    if(maybeAst.isEmpty || unparsedTokens.isEmpty) error("Expected expression")

    Option.apply(BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], unparsedTokens)))

  }


  private def parseSecondaryBinaryOperator(maybeAst: Option[AST], operator: BinaryOperator) : Option[AST]  = {
    unparsedTokens.dequeue()
    if(maybeAst.isEmpty || unparsedTokens.isEmpty) error("Expected expression")

    val newQueue = scala.collection.mutable.Queue[Token]()
    newQueue.enqueue(unparsedTokens.dequeue())
    Option.apply(BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], newQueue)))


  }





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
    if (unparsedTokens.isEmpty) error(s"Expected ${expected} but nothing was found")

  private def checkIfNextTokenIsValid(expectedType: TokenType, expectedSymbol: String): Unit =
    if (unparsedTokens.nonEmpty && unparsedTokens.front.tokenType != expectedType) error(s"Expected ${expectedSymbol} but found ${unparsedTokens.front.tokenType}")

  private def checkIfValidEOL(): Unit = {
    if (unparsedTokens.tail.isEmpty && (!unparsedTokens.front.tokenType.equals(SEMICOLON()))) error("Line should end with semicolon")
//    if (unparsedTokens.tail.isEmpty &&  unparsedTokens.front.tokenType.equals(SEMICOLON())) unparsedTokens.dequeue()
  }
}
