

case class Token(tokenType: TokenType, absoluteRange: AbsoluteRange, lexicalRange: LexicalRange, value: String)

case class AbsoluteRange(from: Int, to: Int)
case class LexicalRange(fromCol: Int, toCol: Int, fromLine: Int, toLine: Int)

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

//WHITESPACE: '\s'
//case class WHITESPACE() extends TokenType { override def toString() : String = "WHITESPACE" }