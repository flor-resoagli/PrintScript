case class Token(tokenType: TokenType, from: Int, to: Int, value: String)


sealed trait TokenType  { def toString (): String }

// DECLARATION: 'let'
case class DECLARATION() extends TokenType { override def toString() : String = "DECLARATION" }

//IDENTIFIER: variable name - [a-zA-Z][a-zA-Z0-9]*
case class IDENTIFIER() extends TokenType { override def toString() : String = "IDENTIFIER" }

//COLON: ':'
case class COLON() extends TokenType { override def toString() : String = "COLON" }

//SEMICOLON: ';'
case class SEMICOLON() extends TokenType { override def toString() : String = "SEMICOLON" }

//EQUAL: '='
case class EQUAL() extends TokenType { override def toString() : String = "EQUAL" }

//NUMBERTYPE: 'number'
case class NUMBERTYPE() extends TokenType { override def toString() : String = "NUMBERTYPE" }

//STRINGTYPE: 'string'
case class STRINGTYPE() extends TokenType { override def toString() : String = "STRINGTYPE" }

//LITERALSTRING: '["-']' + [a-zA-Z0-9]* + '["-']'
case class LITERALSTRING() extends TokenType { override def toString() : String = "LITERALSTRING" }

//LITERALNUMBER: '['0-9]*'
case class LITERALNUMBER() extends TokenType { override def toString() : String = "LITERALNUMBER" }

//SUM: '+'
case class SUM() extends TokenType { override def toString() : String = "SUM" }

//SUB: '-'
case class SUB() extends TokenType { override def toString() : String = "SUB" }

//MUL: '*'
case class MUL() extends TokenType { override def toString() : String = "MUL" }

//DIV: '/'
case class DIV() extends TokenType { override def toString() : String = "DIV" }

//PRINTLN: 'println'
case class PRINTLN() extends TokenType { override def toString() : String = "PRINTLN" }

//LEFTPARENTHESIS: '('
case class LEFTPARENTHESIS() extends TokenType { override def toString() : String = "LEFTPARENTHESIS" }

//RIGHTPARENTHESIS: ')'
case class RIGHTPARENTHESIS() extends TokenType { override def toString() : String = "RIGHTPARENTHESIS" }
//  expr   : term ((PLUS | MINUS) term)*
//  term   : factor ((MUL | DIV) factor)*
//  factor : INTEGER | LPAREN expr RPAREN
sealed trait  AST
case class Addition(left: AST, right: AST) extends AST
case class Subtraction(left: AST, right: AST) extends AST
case class Multiply(left: AST, right: AST) extends AST
case class Divide(left: AST, right: AST) extends AST
case class Equal(left: AST, right: AST) extends AST
case class Variable(value: String) extends AST
case class Constant(value: Double) extends AST
case class Assignation(left: AST, right: AST) extends AST


class Parser() {
  //

  private var currentToken: Token = null
  private val nextTokens = new scala.collection.mutable.Queue()


  def error(msg: String) = {
    throw new Exception(msg)
  }

    def eat(currentToken: TokenType, expected: TokenType) = {
      if (currentToken != expected)  throw new Exception("Expected " + expected + " but found " + currentToken)

    }
  def moveForward(token: Token) = currentToken = {
    if (nextTokens.isEmpty) token
    else nextTokens.dequeue
  }

  def eat(t: Token) = {
    if(t == currentToken) moveForward(t)
    else error("Expected " + t + " but found " + currentToken)
  }




//
//    def nextToken: Token = {
//      if (unparsedTokens.isEmpty) {
//        if (tokens.isEmpty) null
//        else tokens.head
//      } else unparsedTokens.dequeue
//    }
// 2 + 2
// token 2, token +, token 2
//currentToken = 2  list = token +, token 2
// head = +  |   currentToken = 2    |  rest = token 2

  def parse(tokens: List[Token]): AST = {
    if(tokens.isEmpty) error("Empty input")
    return parse(tokens, null)
  }
  def parse(tokens: List[Token], ast: AST): AST = {
    if(tokens.isEmpty) return ast
    currentToken = tokens.head
    return parse(tokens.tail, parseExpression(tokens.tail, ast))
  }

  def parseExpression(tokens: List[Token], ast: AST): AST = {
        tokens match {
          case head :: rest => head.tokenType match {
            case LITERALNUMBER() => parseLiteral(head, ast)
            case LITERALSTRING() => parseLiteral(head, ast)
            case EQUAL() => parseAssignation(rest, ast)
            case SUM() => parseBinary(rest, ast)
            case SUB() => parseBinary(rest, ast)
//            case MUL() => parseBinary(rest, ast
//            case DIV() => parseBinary(rest, ast)
            case _ => parseExpression(rest, ast)

          }
            case _ => error("wrong operator " )
          }


  }
  def parseBinary(tail: List[Token], ast: AST): AST ={
    if(currentToken == null) error("Invalid first argument")
    return new Addition(parseExpression(tail, ast), parseExpression(tail, ast))

  }

  def parseAssignation(value: List[Token], ast: AST): AST = {
    if(currentToken.tokenType != (LITERALNUMBER)) error("No variable to assign") //or LiteralString
    return new Assignation(ast, parse(value, null))
  }

  def parseLiteral(token: Token, ast: AST): AST = {
    token.tokenType match {
        case LITERALNUMBER() => Constant(token.value.toDouble)
        case LITERALSTRING() => Variable(token.value)
        case _ => error("wrong operator " + token)
      }

    }



//  def parseExpression(tokens: List[Token], ast: AST): AST = {
//  val node = parseTerm(tokens)
//    tokens match {
//      case head :: rest => head.tokenType match {
//        case SUM()  => Addition(node, parseTerm(rest))
//        case SUB() =>  Subtraction(node, parseTerm(rest))
//        case EQUAL() => Equal(node, parseTerm(rest))
//        case _ => error("wrong operator " + head)
//      }
//      case _ => node
//    }
//  }

//  def parseTerm(tokens: List[Token]): AST = {
//    val node = parseFactor(tokens)
//    tokens match {
//      case head :: rest => head.tokenType match {
//        case MUL() =>  Multiply(node, parseFactor(rest))
//        case DIV() =>  Divide(node, parseFactor(rest))
////        case
//        case _ => error("wrong operator " + head)
//      }
//      case _ => node
//    }
//  }


//  def parseFactor(tokens: List[Token]): AST = {
//    tokens match {
//      case head :: rest => head match {
//          case TokenType.LITERALNUMBER() => Constant(head.value.toDouble)
//          case TokenType.LITERALSTRING() => Variable(head.value)
//          case TokenType.LEFTPARENTHESIS() => {
//            val node = parseExpression(rest)
//            eat(tokens.head.tokenType, RIGHTPARENTHESIS())
//            node
//          }
//          case _ => error("wrong factor " + head)
//      }
////      case Nil => error("wrong factor " + tokens)
//    }
//  }




}


