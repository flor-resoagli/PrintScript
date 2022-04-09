import scala.annotation.tailrec
import scala.collection.{immutable, mutable}
import scala.collection.mutable.Queue

def isLeftParen(unparsedTokens: mutable.Queue[Token]): Boolean = {
  unparsedTokens.isEmpty || unparsedTokens.front.tokenType ==  LEFTPARENTHESIS()
}

def toImmutable(queue: mutable.Queue[Token]): immutable.Queue[Token] = {
  immutable.Queue(queue.toList:_*)
}

def toMutable(queue: immutable.Queue[Token]): mutable.Queue[Token] = {
  val empty = mutable.Queue.empty[Token]
  var tmp: immutable.Queue[Token] = queue
  while(tmp.nonEmpty) {
    val (token, nextQueue) = tmp.dequeue
    empty.enqueue(token)
    tmp = nextQueue
  }
  empty
}

//trait StatementParser {
//  def canParse(queue: mutable.Queue[Token]): Boolean
//  def parse(queue: mutable.Queue[Token]): AST
//}

//trait StatementParser {
//  def parse(queue: mutable.Queue[Token]): Option[AST]
//}
//
//object DeclarationParser extends StatementParser {
//
//  override def parse(queue: mutable.Queue[Token]): Option[AST] = {
//    if(canParse(queue)) {
//      // parse
//    } else None
//  }
//
//  private def canParse(queue: mutable.Queue[Token]): Boolean = {
//    queue.headOption.exists(token => token.tokenType == DECLARATION())
//  }
//}
//
class Parser() {

  private var unparsedTokens = new scala.collection.mutable.Queue[Token]()

  def error(msg: String): Nothing =
    throw new Exception(msg)

  def parseTokens(tokens: List[Token]): List[AST] = {
    if (tokens.isEmpty) return Nil
    unparsedTokens.enqueueAll(tokens)
    val parseTreesList = List(startParsing(unparsedTokens))
    if (tokensAreLeft(unparsedTokens)) parseTreesList
    else parseTreesList ++ List(startParsing(unparsedTokens))
  }

  private def tokensAreLeft(unparsedTokens: mutable.Queue[Token]): Boolean = unparsedTokens.isEmpty

  private def isSemiColon(token: Token): Boolean = token.tokenType.equals(SEMICOLON())

  private def startParsing(unparsedTokens: mutable.Queue[Token]): AST = {
    checkForSemiColon(unparsedTokens) {
      unparsedTokens.front.tokenType match {
        case DECLARATION() => parseDeclaration(unparsedTokens)
        case IDENTIFIER() => AssignationNode(Variable(unparsedTokens.front.value), parseAssignmentExpression())
        case PRINTLN() => parsePrint(unparsedTokens)
        case _ => error(s"Expected literal, variable or 'let' but found ${unparsedTokens.dequeue().tokenType}")
      }
    }
  }

  private def parsePrint(unparsedTokens: mutable.Queue[Token]): AST = {
    unparsedTokens.dequeue()
    if (!isLeftParen(unparsedTokens)) error("Expected '(' after print")
    val result = PrintNode(parseExpression(Option.empty, unparsedTokens))
    result
  }

  private def parseDeclaration(unparsedTokens: scala.collection.mutable.Queue[Token]): AST = {
    checkIfUnparsedTokensEmpty("variable name")

    unparsedTokens.dequeue()
    unparsedTokens.front.tokenType match {
      case IDENTIFIER() =>
        DeclarationAssignationNode(Variable(unparsedTokens.front.value), parseVariableType(), parseAssignmentExpression())
      case _ => error("Declaration must be followed by a variable")
    }
  }

  private def checkForSemiColon(unparsedTokens: mutable.Queue[Token])(func: => AST): AST = {
    val result = func
    unparsedTokens.headOption match {
      case Some(token) if isSemiColon(token) =>
        unparsedTokens.dequeue()
        result
      case Some(_) => error(s"Line should end with semicolon")
      case None => error(s"Line should end with semicolon")
    }
  }

  private def parseAssignmentExpression(): AST = {
    validateCurrentToken(unparsedTokens, EQUAL(), "=")
    parseExpression(Option.empty[AST], unparsedTokens)

  }

  /*TODO:
     1. Instead of mutating the collection, next call to method could pass "tail" as parameter
   */
  @tailrec
  private def parseExpression(expressionAST: Option[AST], tokensToParse: scala.collection.mutable.Queue[Token]): AST = {

    if (tokensToParse.isEmpty) return expressionAST.get
    tokensToParse.front.tokenType match {
      case LITERALNUMBER() => parseExpression(parseLiteralNumber(tokensToParse), tokensToParse)
      case LITERALSTRING() => parseExpression(Option.apply(ConstantString(tokensToParse.dequeue().value.replace("\"", ""))), tokensToParse)
      case SUM() => parseExpression(parseLowPriorityBinaryOperator(expressionAST, PlusBinaryOperator(), tokensToParse), tokensToParse)
      case SUB() => parseExpression(parseLowPriorityBinaryOperator(expressionAST, MinusBinaryOperator(), tokensToParse), tokensToParse)
      case MUL() => parseExpression(parseHighPriorityBinaryOperator(expressionAST, MultiplyBinaryOperator(), tokensToParse), tokensToParse)
      case DIV() => parseExpression(parseHighPriorityBinaryOperator(expressionAST, DivideBinaryOperator(), tokensToParse), tokensToParse)
      case IDENTIFIER() => parseExpression(Option.apply(Variable(tokensToParse.dequeue().value)), tokensToParse)
      case LEFTPARENTHESIS() => parseExpression(parseLeftParenthesis(), tokensToParse)
      case SEMICOLON() => expressionAST.get
      case _ => error(s"")
    }
  }

  private def parseLiteralNumber(tokensToParse: mutable.Queue[Token]): Option[AST] = {
    Option(ConstantNumb(tokensToParse.dequeue().value.toDouble))
  }

  private def parseLowPriorityBinaryOperator(maybeAst: Option[AST], operator: BinaryOperator, tokensToParse: scala.collection.mutable.Queue[Token]): Option[AST] = {
    tokensToParse.dequeue()
    if (maybeAst.isEmpty || tokensToParse.isEmpty) error("Expected expression")

    Option.apply(BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], tokensToParse)))

  }

  private def parseLeftParenthesis(): Option[AST] = {
    unparsedTokens.dequeue()
    if (unparsedTokens.isEmpty) error("Expected expression")

    val newQueue = scala.collection.mutable.Queue[Token]()
    //    if(!unparsedTokens.contains(_: Token)(_ == RIGHTPARENTHESIS())) error("Expected ')'")

    var buildingAST = Option.empty[AST]
    while (unparsedTokens.nonEmpty && !unparsedTokens.front.tokenType.equals(RIGHTPARENTHESIS())) {
      if (unparsedTokens.front.tokenType == LEFTPARENTHESIS()) buildingAST = Option.apply(parseExpression(Option.empty[AST], unparsedTokens))
      newQueue.enqueue(unparsedTokens.dequeue())
    }

    unparsedTokens.dequeue()
    Option.apply(parseExpression(buildingAST, newQueue))


  }


  private def parseHighPriorityBinaryOperator(maybeAst: Option[AST], operator: BinaryOperator, tokensToParse: scala.collection.mutable.Queue[Token]): Option[AST] = {
    tokensToParse.dequeue()
    if (maybeAst.isEmpty || tokensToParse.isEmpty) error("Expected expression")

    if (tokensToParse.front.tokenType.equals(LEFTPARENTHESIS())) {
      return Option.apply(BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], tokensToParse)))
    }

    val newQueue = scala.collection.mutable.Queue[Token]()
    newQueue.enqueue(tokensToParse.dequeue())
    Option.apply(BinaryOperation(maybeAst.get, operator, parseExpression(Option.empty[AST], newQueue)))

  }


  private def parseVariableType(): VariableTypeNode = {
    validateCurrentToken(unparsedTokens, COLON(), ":")

    unparsedTokens.front.tokenType match {
      case STRINGTYPE() => VariableTypeNode(StringVariableType())
      case NUMBERTYPE() => VariableTypeNode(NumberVariableType())
      case _ => error("Expected variable type but found " + unparsedTokens.dequeue().tokenType)
    }
  }

  /*TODO: Method shouldnt mutate list. Return new one*/
  private def validateCurrentToken(tokens: scala.collection.mutable.Queue[Token], expectedTokenType: TokenType, symbol: String) = {
    tokens.dequeue()
    if (tokens.nonEmpty && tokens.front.tokenType != expectedTokenType) error(s"Expected $symbol but found ${tokens.front.tokenType}")
    tokens.dequeue()
  }

  private def checkIfUnparsedTokensEmpty(expected: String): Unit =
    if (unparsedTokens.isEmpty) error(s"Expected $expected but nothing was found")


}