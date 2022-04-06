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
case class StringVariableType() extends VariableType
case class NumberVariableType() extends VariableType

sealed class BinaryOperator
case class PlusBinaryOperator() extends BinaryOperator
case class MinusBinaryOperator() extends BinaryOperator
case class MultiplyBinaryOperator() extends BinaryOperator
case class DivideBinaryOperator() extends BinaryOperator


/*TODO: Replace VariableNode usages for a String*/
sealed trait  AST
//case class DeclarationAssignationNode(assignationNode: AssignationNode, variableTypeNode: VariableTypeNode)
case class DeclarationAssignationNode(variable: Variable, variableTypeNode: VariableTypeNode, value: AST) extends AST
case class AssignationNode(variable: String, value: AST) extends AST
case class Variable(value: String) extends AST
case class ConstantNumb(value: Double) extends AST
case class ConstantString(value: String) extends AST
case class BinaryOperation(left: AST, operator: BinaryOperator, right: AST) extends AST
case class VariableTypeNode(value: VariableType) extends AST
case class PrintNode(value: AST) extends AST


class Parser() {


  private val unparsedTokens = new scala.collection.mutable.Queue[Token]()


  def error(msg: String): Nothing =
    throw new Exception(msg)

  def parseTokens(tokens: List[Token]): List[AST] = {
     List()
  }

  def parse(tokens: List[Token]): Option[AST] = {
    if(tokens.isEmpty) return Option.empty[AST]
    unparsedTokens.enqueueAll(tokens)
    parseExpression(Option.empty[AST])
  }

  /*TODO: 1. LiteralNumber and string could call their own method that checks forward
  *       2. Identifier allowed as first in line*/
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


  private def parseLiteral(): Option[AST] = {
    unparsedTokens.front.tokenType match {
      case LITERALNUMBER() => Option.apply(ConstantNumb(unparsedTokens.dequeue().value.toDouble))
      case LITERALSTRING() => Option.apply(ConstantString(unparsedTokens.dequeue().value))
      case _ => error(s"Expected literal, variable or 'let' but found ${unparsedTokens.dequeue().tokenType}")
    }
  }

  def parseSum(maybeAst: Option[AST]): Option[AST] = {
    unparsedTokens.dequeue()
    unparsedTokens.front.tokenType match {
//      case SUM() => parseSum(Option.apply(BinaryOperation(maybeAst.get, PlusBinaryOperator(), parseTerm(Option.empty[AST]))))
//      case SUB() => parseSum(Option.apply(BinaryOperation(maybeAst.get, MinusBinaryOperator(), parseTerm(Option.empty[AST]))))
      case _ => error("")
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
      case STRINGTYPE() => VariableTypeNode(StringVariableType())
      case NUMBERTYPE() => VariableTypeNode(NumberVariableType())
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

  //TODO: change error msg
  def checkEOF(ast: Option[AST]): Option[AST] =  {
    unparsedTokens.dequeue()
    if(unparsedTokens.isEmpty  && ast.isDefined) ast
    else error("Expected EOF")
  }





}


