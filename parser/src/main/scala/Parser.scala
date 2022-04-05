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


  private var currentToken: Option[Token] = None
  private val unparsedTokens = new scala.collection.mutable.Queue[Token]()




  def error(msg: String): Nothing = {
    throw new Exception(msg)
  }

  def parse(tokens: List[Token]): Option[AST] = {
    if(tokens.isEmpty) return Option.empty[AST]
    unparsedTokens.enqueueAll(tokens)
    parse(tokens, Option.empty[AST])
  }
  def parse(tokens: List[Token], ast: Option[AST]): Option[AST] = {
    if(unparsedTokens.isEmpty) return ast
    currentToken = Option(unparsedTokens.dequeue())
    parse(tokens.tail, parseExpression(tokens.tail, ast))
  }
  private def parseExpression(tokens: List[Token], ast: Option[AST]): Option[AST] = {
    if(currentToken.isEmpty) return ast
    checkInvalidEOF(tokens)

    currentToken.get.tokenType match {
      case LITERALNUMBER() => Option.apply(ConstantNumb(currentToken.get.value.toDouble))
      case LITERALSTRING() => Option.apply(ConstantString(currentToken.get.value))
      case SEMICOLON() => checkEOF(tokens, ast)
      case DECLARATION() => parseDeclaration(tokens)
      case _ => error("Expected literal or variable but found " + currentToken.get.tokenType)
    }
  }

  //TODO: parseAssignation check if equals and value
  private def parseDeclaration(tokens: List[Token]): Option[AST] = {
    if (currentToken.isEmpty) return error("Expected variable but nothing was found")
    currentToken = Option(unparsedTokens.dequeue())

    currentToken.get.tokenType match {
      case IDENTIFIER() => Option.apply(DeclarationAssignationNode(Variable(currentToken.get.value), parseVariableType(tokens.tail), parseAssignation(tokens.tail.tail)))
      case _ => error("Declaration must be followed by a variable")
    }
  }
  private def parseAssignation(tokens: List[Token]): AST = {
    currentToken = Option(unparsedTokens.dequeue())
    if (currentToken.isEmpty) return error("Expected = but nothing was found")
    if (currentToken.get.tokenType != EQUAL()) error("Expected = but found " + currentToken.get.tokenType)
    currentToken = Option(unparsedTokens.dequeue())


    currentToken.get.tokenType match {
      case LITERALNUMBER() => ConstantNumb(currentToken.get.value.toDouble)
      case LITERALSTRING() => ConstantString(currentToken.get.value)
      case _ => error("Expected equals but found " + currentToken.get.tokenType)
    }
  }

  private def parseVariableType(tokens: List[Token]): VariableTypeNode = {
    currentToken = Option(unparsedTokens.dequeue())
    if (currentToken.isEmpty) return error("Expected : but nothing was found")
    if (currentToken.get.tokenType != COLON()) error("Expected : but found " + currentToken.get.tokenType)
    currentToken = Option(unparsedTokens.dequeue())
    currentToken.get.tokenType match {
      case STRINGTYPE() => VariableTypeNode(StringTypee())
      case NUMBERTYPE() => VariableTypeNode(NumberTypee())
      case _ => error("Expected stringtype or numbertype but found " + currentToken.get.tokenType)
    }
  }


  private def checkInvalidEOF(tokens: List[Token]): Unit = {
    if (tokens.isEmpty && !currentToken.get.tokenType.equals(SEMICOLON())) error("Line should end with semicolon")
  }

  //TODO: change errror msg
  def checkEOF(tokens: List[Token], ast: Option[AST]): Option[AST] =  {
    if(unparsedTokens.isEmpty  && ast.isDefined) ast
    else error("Expected EOF")
  }





}


