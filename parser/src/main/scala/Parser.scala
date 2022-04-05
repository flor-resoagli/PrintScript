import scala.language.postfixOps
//  expr   : term ((PLUS | MINUS) term)*
//  term   : factor ((MUL | DIV) factor)*
//  factor : INTEGER | LPAREN expr RPAREN

//exp: literal | var | op
//op: exp op exp | exp
//operator: + | - | * | /
//literal: String | Number

//trait or enum?
sealed trait VariableType
case class StringTypee() extends VariableType
case class NumberTypee() extends VariableType

//sealed trait expression types
sealed trait ExpressionType


sealed trait  AST
case class Expression()
case class DeclarationAssignationNode(variable: Variable, variableTypeNode: VariableTypeNode, value: AST) extends AST
case class Variable(value: String) extends AST
case class ConstantNumb(value: Double) extends AST
case class ConstantString(value: String) extends AST
case class VariableTypeNode(value: VariableType) extends AST


class Parser() {


  private val unparsedTokens = new scala.collection.mutable.Queue[Token]()


  def error(msg: String): Nothing =
    throw new Exception(msg)

  def parse(tokens: List[Token]): Option[AST] = {
    if(tokens.isEmpty) return Option.empty[AST]
    unparsedTokens.enqueueAll(tokens)
    parseExpression(Option.empty[AST])
  }

  private def parseExpression(ast: Option[AST]): Option[AST] = {
    checkInvalidEOF()
    if(unparsedTokens.isEmpty) return ast

    unparsedTokens.front.tokenType match {
      case LITERALNUMBER() => Option.apply(ConstantNumb(unparsedTokens.dequeue().value.toDouble))
      case LITERALSTRING() => Option.apply(ConstantString(unparsedTokens.dequeue().value))
      case SEMICOLON() => checkEOF(ast)
//      case SUM() => parseSum(ast)
      case DECLARATION() => parseDeclaration()
      case _ => error(s"Expected literal, variable or 'let' but found ${unparsedTokens.dequeue().tokenType}")
    }
  }

  private def parseDeclaration(): Option[AST] = {
    checkIfInvalidEOL("variable name")

    unparsedTokens.dequeue()
    unparsedTokens.front.tokenType match {
      case IDENTIFIER() => Option.apply(DeclarationAssignationNode(Variable(unparsedTokens.dequeue().value), parseVariableType(), parseAssignation()))
      case _ => error("Declaration must be followed by a variable")
    }
  }

  private def parseVariableType(): VariableTypeNode = {
    checkIfNextTokenIsValid(COLON(), ":")

    unparsedTokens.dequeue()
    unparsedTokens.front.tokenType match {
      case STRINGTYPE() => VariableTypeNode(StringTypee())
      case NUMBERTYPE() => VariableTypeNode(NumberTypee())
      case _ => error("Expected variable type but found " + unparsedTokens.dequeue().tokenType)
    }
  }


  private def parseAssignation(): AST = {
    unparsedTokens.dequeue()
    checkIfNextTokenIsValid(EQUAL(), "=")

    unparsedTokens.dequeue()
    val ast = parseExpression(Option.empty[AST])
    ast.get
  }

  private def checkIfNextTokenIsValid(expectedType: TokenType, expectedSymbol: String): Unit =
    if (unparsedTokens.nonEmpty && unparsedTokens.front.tokenType != expectedType) error(s"Expected ${expectedSymbol} but found ${unparsedTokens.front.tokenType}")


  private def checkIfInvalidEOL(expected: String): Unit  =
    if (unparsedTokens.isEmpty ) error(s"Expected ${expected} but nothing was found")


  private def checkInvalidEOF(): Unit = {
    if (unparsedTokens.tail.isEmpty && !unparsedTokens.front.tokenType.equals(SEMICOLON())) error("Line should end with semicolon")
  }

  //TODO: change errror msg
  def checkEOF(ast: Option[AST]): Option[AST] =  {
    unparsedTokens.dequeue()
    if(unparsedTokens.isEmpty  && ast.isDefined) ast
    else error("Expected EOF")
  }





}


