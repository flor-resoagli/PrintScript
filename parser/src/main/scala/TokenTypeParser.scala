import scala.::
import scala.collection.{immutable, mutable}
import scala.collection.immutable.Queue
import scala.util.control.Breaks.{break, breakable}
trait TokenTypeParser {
  def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult
  def isValid(unparsedTokens: Queue[Token]): Boolean
  def getNext(): List[TokenType]
}
/*TODO:
   1.Possible optimization: Check if empty before validating all parsers for better performance
*  2. The StringType and NumberType could e a single parser that receives the tokenType and correct Node*/

type ParsingResult= (AST, Queue[Token])
def error(msg: String): Nothing = {
  throw new Exception (msg)
}
def checkIfNextIsExpected(expected: String, tokens: Queue[Token]): Unit = if (tokens.isEmpty) error(s"Expected $expected but nothing was found")
def validateCurrentToken(tokens: Queue[Token], expectedTokenType: TokenType, symbol: String): Queue[Token] = {
  if(tokens.isEmpty)error(s"Expected $symbol")
  var (front, tail) = tokens.dequeue
  if (tail.nonEmpty && front.tokenType != expectedTokenType) error(s"Expected $symbol but found ${front.tokenType}")
  tail
}
def isEmpty(result: (AST, Queue[Token])): Boolean = {
  result._1 == EmptyNode()
}

def isSemicolon(newTokens: Queue[Token]): Boolean = {
  (newTokens.head.tokenType == SEMICOLON())
}



case class DeclarationParser(varibleTypes: List[TokenTypeParser], validExpressions: List[TokenType], parserProvider: ParserProvider, declaringTokens: List[TokenType]) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (let, tokens) = unparsedTokens.dequeue
    checkIfNextIsExpected("variable name", tokens)
    val (variable, newTokens) = tokens.dequeue

    val (varibleTypeNode, tokensWithoutVaribleType) = VariableTypeParser(varibleTypes).parse(newTokens, buildingAST)

    val expressionResult: ParsingResult = ValueAssignationParser(validExpressions, parserProvider).parse(tokensWithoutVaribleType, EmptyNode())

    (DeclarationAssignationNode(Variable(variable.value), varibleTypeNode, expressionResult._1), expressionResult._2)

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => declaringTokens contains token.tokenType)
  }
  def getNext(): List[TokenType] = {
    validExpressions
  }
}


case class ValueAssignationParser(validNextTokens: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (equal, newTokens) = unparsedTokens.dequeue

    var result: ParsingResult = ExpressionParser(validNextTokens, parserProvider).parse(newTokens, buildingAST)

//    val removedSemicolon = validateCurrentToken(result._2, SEMICOLON(), "semicolon")
//    (result._1, removedSemicolon)

    result
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == EQUAL())
  }
  def getNext(): List[TokenType] = {
    validNextTokens
  }
}

case class VariableReassignationParser(expressions: List[TokenType], parserProvider: ParserProvider, endingToken: TokenType) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variable, newTokens) = unparsedTokens.dequeue
    val (expAst, tokens) = ValueAssignationParser(expressions, parserProvider).parse(newTokens, EmptyNode())
//    val validatedTokens = validateCurrentToken(tokens, endingToken, "semicolon")
    (AssignationNode(Variable(variable.value), expAst), tokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == IDENTIFIER())
  }
  def getNext(): List[TokenType] = {
    expressions
  }
}
case class IfParser(expressions: List[TokenType], parserProvider: ParserProvider, conditionParser: ConditionParser) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variable, newTokens) = unparsedTokens.dequeue

    val (condition, tokens) = conditionParser.parse(newTokens, EmptyNode())



//    val node = IfNode(cond, , )

//    (node, resultTokens)
    (condition, tokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == IF())
  }
  def getNext(): List[TokenType] = {
    expressions
  }
}

case class ConditionParser() extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (condition, newTokens) = unparsedTokens.dequeue
    val (rightParenthesis, newTokens2) = unparsedTokens.dequeue

    val node = condition.value match {
      case "true" => ConstantBoolean(true)
      case "false" => ConstantBoolean(false)
      case _ => Variable(condition.value)
    }

    (node, newTokens2)


  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == BOOLEANTYPE())
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}

case class VariableTypeParser(parsersToCall: List[TokenTypeParser]) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (colon, newTokens) = unparsedTokens.dequeue

    var result: ParsingResult = (EmptyNode(), newTokens)
    parsersToCall.foreach(parser => {
      if (parser.isValid(newTokens)) {
        result = parser.parse(newTokens, EmptyNode())
      }
    })
    if(result._1.isEmpty()) error(s"Expected variable type but found nothing")

    (result._1, result._2)
  }

    def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == COLON())
  }


  def getNext(): List[TokenType] = {
    Nil
  }
}

case class ConstantNumbParser() extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variableType, newTokens) = unparsedTokens.dequeue

    (VariableTypeNode(ConstantNumberType()), newTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == NUMBERTYPE())
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}
case class ConstantStringParser() extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variableType, newTokens) = unparsedTokens.dequeue

    (VariableTypeNode(ConstantStringType()), newTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == STRINGTYPE())
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}


case class StringTypeParser() extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variableType, newTokens) = unparsedTokens.dequeue

    (VariableTypeNode(StringVariableType()), newTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == STRINGTYPE())
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}
case class NumberTypeParser() extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variableType, newTokens) = unparsedTokens.dequeue

    (VariableTypeNode(NumberVariableType() ), newTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == NUMBERTYPE())
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}



case class PrintParser(validTokens: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (numberToken, newTokens) = unparsedTokens.dequeue
//    val (, newTokens2) = unparsedTokens.dequeue

    var result: ParsingResult = ExpressionParser(validTokens, parserProvider).parse(newTokens, buildingAST)

    (PrintNode(result._1), result._2)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == PRINTLN())
  }
  def getNext(): List[TokenType] = {
    validTokens
  }
}

case class LiteralNumbParser(validTokens: List[TokenType], parserProvider: ParserProvider, endingParser: TokenTypeParser) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (numberToken, newTokens) = unparsedTokens.dequeue
    val node = (ConstantNumb(numberToken.value.toDouble))

    ExpressionParser(validTokens, parserProvider).parse(newTokens, node)

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == LITERALNUMBER())
  }
  def getNext(): List[TokenType] = {
    validTokens
  }

}
case class VariableParser(validTokens: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variable, newTokens) = unparsedTokens.dequeue
    val node = (Variable(variable.value))
    ExpressionParser(validTokens, parserProvider).parse(newTokens, node)

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == IDENTIFIER())
  }

  def getNext(): List[TokenType] = {
    Nil
  }
}

case class LiteralStringParser(validTokens: List[TokenType], parserProvider: ParserProvider, endingParser: TokenTypeParser) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (strToken, newTokens) = unparsedTokens.dequeue
    val node = (ConstantString(strToken.value.replace("\"", "")))

    ExpressionParser(validTokens, parserProvider).parse(newTokens, node)

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == LITERALSTRING())
  }
  def getNext(): List[TokenType] = {
    validTokens
  }
}


case class SemicolonParser(parsersToCall: List[TokenType]) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    isValid(unparsedTokens)
    val (semicolon, newTokens) = unparsedTokens.dequeue
    (buildingAST, newTokens)

  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    isSemicolon(unparsedTokens)
//    if (isSemicolon(unparsedTokens)) true
//    else error(s"Expected semicolon but found ${unparsedTokens.headOption.getOrElse("nothing")}")
    //    unparsedTokens.headOption.exists(token => token.tokenType == SEMICOLON())
  }
  def getNext(): List[TokenType] = {
    parsersToCall
  }

}


case class LeftParenthesisParser(parsersToCall: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    if(unparsedTokens.isEmpty) return (buildingAST, unparsedTokens)
    val (token, newTokens) = unparsedTokens.dequeue
    if (newTokens.isEmpty) error("Expected expression")


    var resultAux: ParsingResult = (EmptyNode(), newTokens)
    val auxQueue= mutable.Queue[Token]()
    auxQueue ++= newTokens
    val accumulatorQueue =  mutable.Queue[Token]()

    while(notRightParenthesis(auxQueue)){
      if (isLeftParen(auxQueue)) {return this.parse(toImmutable(auxQueue), EmptyNode())}
      accumulatorQueue.enqueue(auxQueue.dequeue())
    }

    auxQueue.dequeue

    val accumulatorHead = accumulatorQueue.front

    val parenthesisResult = (ExpressionParser(parsersToCall, parserProvider).parse(toImmutable(accumulatorQueue), resultAux._1)._1, toImmutable(auxQueue))

    val previousTokenValidParsers = parserProvider.getTokenParser(accumulatorHead.tokenType, IdentifierState.InUse).getNext()
    ExpressionParser(previousTokenValidParsers, parserProvider).parse(toImmutable(auxQueue), parenthesisResult._1)
    //    RightParenthesisParser(parsersToCall, parserProvider, SEMICOLON()).parse(result._2, result._1)

  }
  def toImmutable(queue: mutable.Queue[Token]): immutable.Queue[Token] = {
    immutable.Queue(queue.toList: _*)
  }
  def isLeftParen(unparsedTokens: mutable.Queue[Token]): Boolean = {
    unparsedTokens.isEmpty || unparsedTokens.front.tokenType == LEFTPARENTHESIS()
  }
  def notRightParenthesis(unparsedTokens: mutable.Queue[Token]): Boolean = {
    unparsedTokens.nonEmpty && !unparsedTokens.front.tokenType.equals(RIGHTPARENTHESIS())
  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == LEFTPARENTHESIS())
  }

  def getNext(): List[TokenType] = {
    parsersToCall
  }

}
case class RightParenthesisParser(parsersToCall: List[TokenType], parserProvider: ParserProvider, endingToken: TokenType)  extends TokenTypeParser{

   def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    if(unparsedTokens.isEmpty) return (buildingAST, unparsedTokens)
    val (token, newTokens) = unparsedTokens.dequeue

//    if(!buildingAST.isEmpty() && token.tokenType != RIGHTPARENTHESIS()) error("Missing closing right parenthesis")

     ExpressionParser(parsersToCall, parserProvider).parse(newTokens, buildingAST)

  }
   def isValid(unparsedTokens: Queue[Token]): Boolean = {
     unparsedTokens.headOption.exists(token => token.tokenType == RIGHTPARENTHESIS())
   }

    def getNext(): List[TokenType] = {
      parsersToCall
    }
}

case class ExpressionParser(expectedTokens: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser {
  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    if(unparsedTokens.isEmpty) return (buildingAST, unparsedTokens)
    if isSemiColon(unparsedTokens.front) then return (buildingAST, unparsedTokens)

    val parsersToCall = parserProvider.getParsers(expectedTokens, IdentifierState.InUse)
//    parsersToCall.foreach(p => p.getNext().contains(unparsedTokens.head.tokenType))
    var result: ParsingResult = (EmptyNode(), unparsedTokens)
    breakable {
      parsersToCall.foreach(parser => {
        if (unparsedTokens.nonEmpty && parser.isValid(unparsedTokens)) {
          result = parser.parse(unparsedTokens, buildingAST)
          break
        }
      })
    }
    if(result._1.isEmpty()) error("Expected expression")

    parse(result._2, result._1)

  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.nonEmpty
  }
  private def isSemiColon(token: Token): Boolean = token.tokenType.equals(SEMICOLON())

  def getNext(): List[TokenType] = {
    expectedTokens
  }
}

case class AdditionParser(validNextTokens: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (plus, newTokens) = unparsedTokens.dequeue
    if (buildingAST.isEmpty() || newTokens.isEmpty) error("Expected expression")


    val result = ExpressionParser(validNextTokens, parserProvider).parse(newTokens, buildingAST)
    (BinaryOperation(buildingAST, PlusBinaryOperator(),  result._1), result._2)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == SUM())
      && unparsedTokens.tail.headOption.exists(token => List(LITERALSTRING(), LITERALNUMBER(), IDENTIFIER()) contains token.tokenType)

  }
  def getNext(): List[TokenType] = {
    validNextTokens
  }

}
case class HighPriorityOperationParser(validNextTokens: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (op, newTokens) = unparsedTokens.dequeue

    val opNode = op.tokenType match {
      case MUL() => MultiplyBinaryOperator()
      case DIV() => DivideBinaryOperator()
      case _ => error("Expected multiplication or division operator")
    }

    if (isLeftParen(newTokens)) {
      val exp = ExpressionParser(validNextTokens, parserProvider).parse(newTokens, EmptyNode())
      return (BinaryOperation(buildingAST, opNode, exp._1), exp._2)
    }

    val (elem, newTokens2) = unparsedTokens.dequeue
    val exp = ExpressionParser(validNextTokens, parserProvider).parse(newTokens2, EmptyNode())
    (BinaryOperation(buildingAST, opNode, exp._1), exp._2)

  }

  def isLeftParen(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.isEmpty || unparsedTokens.front.tokenType == LEFTPARENTHESIS()
  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(List(DIV(), MUL()) contains _.tokenType)

  }

  def getNext(): List[TokenType] = {
    validNextTokens
  }
}

case class SubstractionParser(validNextTokens: List[TokenType], parserProvider: ParserProvider) extends TokenTypeParser{

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (minus, newTokens) = unparsedTokens.dequeue
    if (buildingAST.isEmpty() || unparsedTokens.isEmpty) error("Expected expression")

    val result = ExpressionParser(validNextTokens, parserProvider).parse(newTokens, buildingAST)
    (BinaryOperation(buildingAST, PlusBinaryOperator(),  result._1), result._2)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == SUB())
      && unparsedTokens.tail.headOption.exists(token => List(LITERALSTRING(), LITERALNUMBER(), IDENTIFIER()) contains token.tokenType)

  }
  def getNext(): List[TokenType] = {
    validNextTokens
  }

}