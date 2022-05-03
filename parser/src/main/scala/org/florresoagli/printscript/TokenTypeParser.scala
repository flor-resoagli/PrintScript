package org.florresoagli.printscript

import org.florresoagli.printscript.{AST, ParserProvider, Token, TokenType}

import scala.::
import scala.collection.immutable.Queue
import scala.collection.{immutable, mutable}
import scala.util.control.Breaks.{break, breakable}

/*TODO:
   1.Possible optimization: Check if empty before validating all parsers for better performance
 *  2. The StringType and NumberType could e a single parser that receives the tokenType and correct Node*/

trait TokenTypeParser {
  def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult
  def isValid(unparsedTokens: Queue[Token]): Boolean
  def getNext(): List[TokenType]
}

type ParsingResult = (AST, Queue[Token])
def error(msg: String): Nothing = {
  throw new Exception(msg)
}
def toMutable(queue: immutable.Queue[Token]): mutable.Queue[Token] = {
  val empty = mutable.Queue.empty[Token]
  var tmp: immutable.Queue[Token] = queue
  while (tmp.nonEmpty) {
    val (token, nextQueue) = tmp.dequeue
    empty.enqueue(token)
    tmp = nextQueue
  }
  empty
}
def validateCurrentToken(
  tokens: Queue[Token],
  expectedTokenType: TokenType,
  symbol: String
): Queue[Token] = {
  if (tokens.isEmpty) error(s"Expected $symbol")
  var (front, tail) = tokens.dequeue
  if (tail.nonEmpty && front.tokenType != expectedTokenType)
    error(s"Expected $symbol but found ${front.tokenType}")
  tail
}
def isEmpty(result: (AST, Queue[Token])): Boolean = {
  result._1 == EmptyNode()
}
def toImmutable(queue: mutable.Queue[Token]): immutable.Queue[Token] = {
  immutable.Queue(queue.toList: _*)
}

def isSemicolon(newTokens: Queue[Token]): Boolean = {
  (newTokens.head.tokenType == SEMICOLON())
}

case class DeclarationParser(
  varibleTypes: List[TokenTypeParser],
  validExpressions: List[TokenType],
  parserProvider: ParserProvider,
  declaringTokens: List[TokenType]
) extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (let, tokens) = unparsedTokens.dequeue
    val (variable, newTokens) = tokens.dequeue

    val (varibleTypeNode, tokensWithoutVaribleType) =VariableTypeParser(varibleTypes).parse(newTokens, buildingAST)

      if(tokensWithoutVaribleType.head.tokenType == SEMICOLON()) then  return   (
        DeclarationAssignationNode(Variable(variable.value), varibleTypeNode, EmptyNode()),
        tokensWithoutVaribleType
      )

    val expressionResult: ParsingResult = if(ValueAssignationParser(validExpressions, parserProvider).isValid(tokensWithoutVaribleType)) then ValueAssignationParser(validExpressions, parserProvider)
      .parse(tokensWithoutVaribleType, EmptyNode()) else error("Expected expression")

    (
      DeclarationAssignationNode(Variable(variable.value), varibleTypeNode, expressionResult._1),
      expressionResult._2
    )

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => declaringTokens contains token.tokenType)
  }
  def getNext(): List[TokenType] = {
    validExpressions
  }
}

case class ValueAssignationParser(validNextTokens: List[TokenType], parserProvider: ParserProvider)
    extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (equal, newTokens) = unparsedTokens.dequeue

    var result: ParsingResult =
      ExpressionParser(validNextTokens, parserProvider).parse(newTokens, buildingAST)

//    val removedSemicolon = validateCurrentToken(result._2, SEMICOLON(), "semicolon")
//    (result._1, removedSemicolon)

    result
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    (unparsedTokens.headOption.exists(token => token.tokenType == EQUAL()) && validNextTokens.contains(unparsedTokens.tail.head.tokenType))
  }
  def getNext(): List[TokenType] = {
    validNextTokens
  }
}

case class VariableReassignationParser(
  expressions: List[TokenType],
  parserProvider: ParserProvider,
  endingToken: TokenType
) extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variable, newTokens) = unparsedTokens.dequeue
    val (expAst, tokens) =
      ValueAssignationParser(expressions, parserProvider).parse(newTokens, EmptyNode())
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

case class IfParser(
  declarationPArsers: List[TokenType],
  parserProvider: ParserProvider,
  conditionParser: ConditionParser
) extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (ifToken, newTokens) = unparsedTokens.dequeue

    val (condition, tokens) = conditionParser.parse(newTokens, EmptyNode())
    val tokensAfterLetfBrace = validateCurrentToken(tokens, LEFTBRACE(), "left brace")

    var auxQueue: _root_.scala.collection.mutable.Queue[Token] = getMutableQueue(
      tokensAfterLetfBrace
    )
    val accumulatorQueue = mutable.Queue[Token]()
    var maybeInnerIfNode: AST = EmptyNode()

    breakable {
      while (!isRightBrace(auxQueue)) {
        if (isIfCondition(auxQueue)) {
          val (innerIfNode, newQueue) = this.parse(toImmutable(auxQueue), EmptyNode())
          maybeInnerIfNode = innerIfNode
          if(newQueue.isEmpty) then break()
          auxQueue = toMutable(newQueue)
          auxQueue.remove(auxQueue.size-1)
          if (isRightBrace(mutable.Queue(auxQueue.front))) break()
//          if (isSemicolon(toImmutable(auxQueue))) auxQueue.dequeue
//          auxQueue.enqueue(rightPArenthesis)
//          break()

        }
        accumulatorQueue.enqueue(auxQueue.dequeue())
      }
    }
    val innerTokensParseResult: List[AST] =
      Parser(declarationPArsers, parserProvider).parseTokens(accumulatorQueue.toList)

    auxQueue.dequeue()

    val elseParser = ElseParser(declarationPArsers, parserProvider)
    val (elseNodes, tokensAfterElse): (List[AST], Queue[Token]) = if (elseParser.isValid(toImmutable(auxQueue))) then elseParser.parse(toImmutable(auxQueue), EmptyNode()) else (List(), toImmutable(auxQueue))
    auxQueue = toMutable((tokensAfterElse))

    auxQueue.enqueue(Token(SEMICOLON(), AbsoluteRange(0, 0), LexicalRange(0, 0, 0, 0), ";"))

    val node =
      if !(maybeInnerIfNode.isEmpty()) then
        (List(maybeInnerIfNode) ++ innerTokensParseResult)
      else innerTokensParseResult

    (IfNode(condition, node, elseNodes), toImmutable(auxQueue))

//    parse(toImmutable(auxQueue), IfNode(condition, innerTokensParseResult, Nil))
  }

  def isIfCondition(unparsedTokens: mutable.Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == IF())
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == IF())
  }
  def getNext(): List[TokenType] = {
    declarationPArsers
  }
}

def getMutableQueue(tokensAfterLetfBrace: Queue[Token]): mutable.Queue[Token] = {
  var auxQueue = mutable.Queue[Token]()
  auxQueue ++= tokensAfterLetfBrace
  auxQueue
}
def isRightBrace(unparsedTokens: mutable.Queue[Token]): Boolean = {
//      if(unparsedTokens.isEmpty) error("missing right brace")
  unparsedTokens.nonEmpty && unparsedTokens.front.tokenType.equals(RIGHTBRACE())
}
case class ElseParser(declarationPArsers: List[TokenType], parserProvider: ParserProvider) {

  def parse(unparsedTokens: Queue[Token], buildingAST: AST): (List[AST], Queue[Token]) = {
    val (elseToken, tokens) = unparsedTokens.dequeue
    val tokensAfterLetfBrace = validateCurrentToken(tokens, LEFTBRACE(), "left brace")

    var auxQueue: mutable.Queue[Token] = getMutableQueue(tokensAfterLetfBrace)
    val accumulatorQueue = mutable.Queue[Token]()
    var maybeInnerIfResult: ParsingResult = (EmptyNode(), unparsedTokens)

    breakable {
      while (!isRightBrace(auxQueue)) {
        accumulatorQueue.enqueue(auxQueue.dequeue())
      }
    }
    val innerTokensParseResult: List[AST] =
      Parser(declarationPArsers, parserProvider).parseTokens(accumulatorQueue.toList)

    val tokensAfterRightBrace =
      validateCurrentToken(toImmutable(auxQueue), RIGHTBRACE(), "right brace")

    (innerTokensParseResult, tokensAfterRightBrace)

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == ELSE())
  }
}

case class ConditionParser() extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val tokens = validateCurrentToken(unparsedTokens, LEFTPARENTHESIS(), "left parenthesis")
    val (condition, newTokens) = tokens.dequeue
    val expTokens = validateCurrentToken(newTokens, RIGHTPARENTHESIS(), "right parenthesis")

    val node: AST = condition.value match {
      case "true"  => ConstantBoolean(true)
      case "false" => ConstantBoolean(false)
      case _       => Variable(condition.value)
    }

    (node, expTokens)

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == BOOLEANTYPE())
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}

case class VariableTypeParser(parsersToCall: List[TokenTypeParser]) extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (colon, newTokens) = unparsedTokens.dequeue

    var result: ParsingResult = (EmptyNode(), newTokens)
    parsersToCall.foreach(parser => {
      if (parser.isValid(newTokens)) {
        result = parser.parse(newTokens, EmptyNode())
      }
    })
    if (result._1.isEmpty()) error(s"Expected variable type but found nothing")

    (result._1, result._2)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == COLON())
  }

  def getNext(): List[TokenType] = {
    Nil
  }
}

case class ConstantNumbParser() extends TokenTypeParser {

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

case class StringTypeParser(validTokens: List[TokenType]) extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variableType, newTokens) = unparsedTokens.dequeue

    (VariableTypeNode(StringVariableType()), newTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => validTokens.contains(token.tokenType))
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}
case class NumberTypeParser() extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variableType, newTokens) = unparsedTokens.dequeue

    (VariableTypeNode(NumberVariableType()), newTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
      (unparsedTokens.headOption.exists(token => token.tokenType == NUMBERTYPE()))
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}

case class BooleanTypeParser() extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (variableType, newTokens) = unparsedTokens.dequeue

    (VariableTypeNode(BooleanVariableType()), newTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
      (unparsedTokens.headOption.exists(token => token.tokenType == BOOLEANTYPE()))
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}

case class PrintParser(validTokens: List[TokenType], parserProvider: ParserProvider)
    extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (println, newTokens) = unparsedTokens.dequeue
//    val (, newTokens2) = unparsedTokens.dequeue

    var result: ParsingResult =
      ExpressionParser(validTokens, parserProvider).parse(newTokens, buildingAST)

    (PrintNode(result._1), result._2)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == PRINTLN())
  }
  def getNext(): List[TokenType] = {
    validTokens
  }
}

case class ReadInputParser(validParsers: List[TokenTypeParser], parserProvider: ParserProvider)
    extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (readInput, tokens) = unparsedTokens.dequeue
    val newTokens = validateCurrentToken(tokens, LEFTPARENTHESIS(), "Expected '(' after read input")
    val (input, newTokens2) = newTokens.dequeue

//    var result: ParsingResult = parserProvider.getParsers(validTokens, org.florresoagli.printscript.IdentifierState.InUse).find(parser => parser.isValid(Queue(input))).get.parse(Queue(input), buildingAST)
    var result: ParsingResult =
      validParsers.find(parser => parser.isValid(Queue(input))).get.parse(Queue(input), buildingAST)

    val finalTokens = validateCurrentToken(newTokens2, RIGHTPARENTHESIS(), "Expected ')'")

    (ReadInputNode(result._1), finalTokens)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == READINPUT())
  }
  def getNext(): List[TokenType] = {
    Nil
  }
}

case class LiteralNumbParser(
  validTokens: List[TokenType],
  parserProvider: ParserProvider,
  endingParser: TokenTypeParser
) extends TokenTypeParser {

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
case class ConstantBooleanParser() extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (bool, newTokens) = unparsedTokens.dequeue
    val boolValue: Boolean = bool.value match {
      case "true"  => true
      case "false" => false
      case _       => error("Unsupported boolean")

    }
    val node = (ConstantBoolean(boolValue))

    (node, newTokens)

  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == LITERALBOOLEAN())
  }
  def getNext(): List[TokenType] = {
    Nil
  }

}
case class VariableParser(validTokens: List[TokenType], parserProvider: ParserProvider)
    extends TokenTypeParser {

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

case class LiteralStringParser(
  validTokens: List[TokenType],
  parserProvider: ParserProvider,
  endingParser: TokenTypeParser
) extends TokenTypeParser {

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
case class ConstantStringParser() extends TokenTypeParser {

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


case class LeftParenthesisParser(parsersToCall: List[TokenType], parserProvider: ParserProvider)
    extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    if (unparsedTokens.isEmpty) return (buildingAST, unparsedTokens)
    val (token, newTokens) = unparsedTokens.dequeue
    if (newTokens.isEmpty) error("Expected expression")

    var resultAux: ParsingResult = (EmptyNode(), newTokens)
    var auxQueue: _root_.scala.collection.mutable.Queue[Token] = getMutableQueue(newTokens)
    val accumulatorQueue = mutable.Queue[Token]()

    while (notRightParenthesis(auxQueue)) {
      if (isLeftParen(auxQueue)) { return this.parse(toImmutable(auxQueue), EmptyNode()) }
      accumulatorQueue.enqueue(auxQueue.dequeue())
    }

    auxQueue = toMutable(validateCurrentToken(toImmutable(auxQueue), RIGHTPARENTHESIS(), ")"))

    val accumulatorHead = accumulatorQueue.front

    val parenthesisResult = (
      ExpressionParser(parsersToCall, parserProvider)
        .parse(toImmutable(accumulatorQueue), resultAux._1)
        ._1,
      toImmutable(auxQueue)
    )

    val lastTokenValidParsers =
      parserProvider.getTokenParser(accumulatorHead.tokenType, IdentifierState.InUse).getNext()
    ExpressionParser(lastTokenValidParsers, parserProvider).parse(
      toImmutable(auxQueue),
      parenthesisResult._1
    )
    //    org.florresoagli.printscript.RightParenthesisParser(parsersToCall, parserProvider, SEMICOLON()).parse(result._2, result._1)

  }

  def isLeftParen(unparsedTokens: mutable.Queue[Token]): Boolean = {
    unparsedTokens.isEmpty || unparsedTokens.front.tokenType == LEFTPARENTHESIS()
  }
  def notRightParenthesis(unparsedTokens: mutable.Queue[Token]): Boolean = {
    unparsedTokens.nonEmpty && !unparsedTokens.front.tokenType.equals(RIGHTPARENTHESIS())
  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
   ((unparsedTokens.headOption.exists(token => token.tokenType == LEFTPARENTHESIS())))
//     && unparsedTokens.tail.headOption.exists(token => if(!parsersToCall.contains(token.tokenType)) error("Expected expression") else true))

  }

  def getNext(): List[TokenType] = {
    parsersToCall
  }

}
case class RightParenthesisParser(
  parsersToCall: List[TokenType],
  parserProvider: ParserProvider,
  endingToken: TokenType
) extends TokenTypeParser {

  def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    if (unparsedTokens.isEmpty) return (buildingAST, unparsedTokens)
    val (token, newTokens) = unparsedTokens.dequeue

//    if(!buildingAST.isEmpty() && token.tokenType != RIGHTPARENTHESIS()) error("Missing closing right parenthesis")

    ExpressionParser(parsersToCall, parserProvider).parse(newTokens, buildingAST)

  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    if(!parsersToCall.contains(unparsedTokens.tail.head.tokenType))error("Expected expression")
    (unparsedTokens.headOption.exists(token => token.tokenType == RIGHTPARENTHESIS()))
  }

  def getNext(): List[TokenType] = {
    parsersToCall
  }
}

case class SemicolonParser(tokens: List[TokenType]) extends TokenTypeParser {
  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
  if(buildingAST.isEmpty()) error("Expected expression")
  (buildingAST, unparsedTokens)


  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.headOption.exists(token => token.tokenType == SEMICOLON())
  }

  def getNext(): List[TokenType] = {
    Nil
  }

}
case class ExpressionParser(expectedTokens: List[TokenType], parserProvider: ParserProvider)
    extends TokenTypeParser {
  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    if (unparsedTokens.isEmpty) return (buildingAST, unparsedTokens)
    if (unparsedTokens.isEmpty & buildingAST.isEmpty()) error("Expected expression")

    if (isSemiColon(unparsedTokens.front)) then return (buildingAST, unparsedTokens)

    val parsersToCall = parserProvider.getParsers(expectedTokens, IdentifierState.InUse)

    var result: ParsingResult = (EmptyNode(), unparsedTokens)
    result = parsersToCall
            .find(parser => parser.isValid(unparsedTokens) && unparsedTokens.nonEmpty)
            .get
            .parse(unparsedTokens, buildingAST)
    
    if (result._1.isEmpty()) error("Expected expression")

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

case class AdditionParser(validNextTokens: List[TokenType], parserProvider: ParserProvider)
    extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (plus, newTokens) = unparsedTokens.dequeue
    if (buildingAST.isEmpty() || newTokens.isEmpty) error("Expected expression")

    val result = ExpressionParser(validNextTokens, parserProvider).parse(newTokens, buildingAST)
    (BinaryOperation(buildingAST, PlusBinaryOperator(), result._1), result._2)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
      unparsedTokens.headOption.exists(token => token.tokenType == SUM())
    && unparsedTokens.tail.headOption.exists(token =>
      List(LITERALSTRING(), LITERALNUMBER(), IDENTIFIER()) contains token.tokenType
    )

  }
  def getNext(): List[TokenType] = {
    validNextTokens
  }

}
case class HighPriorityOperationParser(
  validNextTokens: List[TokenType],
  parserProvider: ParserProvider
) extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (op, newTokens) = unparsedTokens.dequeue

    val opNode = op.tokenType match {
      case MUL() => MultiplyBinaryOperator()
      case DIV() => DivideBinaryOperator()
      case _     => error("Expected multiplication or division operator")
    }

    if (isLeftParen(newTokens)) {
      val exp = ExpressionParser(validNextTokens, parserProvider).parse(newTokens, EmptyNode())
      return (BinaryOperation(buildingAST, opNode, exp._1), exp._2)
    }

    val (elem, newTokens2) = newTokens.dequeue
//    val exp = org.florresoagli.printscript.ExpressionParser(validNextTokens, parserProvider).parse(newTokens, EmptyNode())
    var exp: ParsingResult = parserProvider
      .getParsers(validNextTokens, IdentifierState.InUse)
      .find(parser => parser.isValid(Queue(elem)))
      .get
      .parse(Queue(elem), buildingAST)

    (BinaryOperation(buildingAST, opNode, exp._1), newTokens2)

  }

  def isLeftParen(unparsedTokens: Queue[Token]): Boolean = {
    unparsedTokens.isEmpty || unparsedTokens.front.tokenType == LEFTPARENTHESIS()
  }
  def isValid(unparsedTokens: Queue[Token]): Boolean = {
    (unparsedTokens.headOption.exists(List(DIV(), MUL()) contains _.tokenType))
  }

  def getNext(): List[TokenType] = {
    validNextTokens
  }
}

case class SubstractionParser(validNextTokens: List[TokenType], parserProvider: ParserProvider)
    extends TokenTypeParser {

  override def parse(unparsedTokens: Queue[Token], buildingAST: AST): ParsingResult = {
    val (minus, newTokens) = unparsedTokens.dequeue
    if (buildingAST.isEmpty() || unparsedTokens.isEmpty) error("Expected expression")

    val result = ExpressionParser(validNextTokens, parserProvider).parse(newTokens, buildingAST)
    (BinaryOperation(buildingAST, MinusBinaryOperator(), result._1), result._2)
  }

  def isValid(unparsedTokens: Queue[Token]): Boolean = {
      unparsedTokens.headOption.exists(token => token.tokenType == SUB())
    && unparsedTokens.tail.headOption.exists(token =>
      List(LITERALSTRING(), LITERALNUMBER(), IDENTIFIER()) contains token.tokenType
    )

  }
  def getNext(): List[TokenType] = {
    validNextTokens
  }

}
