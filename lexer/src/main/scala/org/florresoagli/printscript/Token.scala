package org.florresoagli.printscript

case class Token(
  tokenType: TokenType,
  absoluteRange: AbsoluteRange,
  lexicalRange: LexicalRange,
  value: String
)

case class AbsoluteRange(from: Int, to: Int)
case class LexicalRange(fromCol: Int, toCol: Int, fromLine: Int, toLine: Int)

sealed trait TokenType { def toString(): String }

// lexer.DECLARATION: 'let'
case class DECLARATION() extends TokenType { override def toString(): String = "lexer.DECLARATION" }

//lexer.CONSTANT: 'const'
case class CONSTANT() extends TokenType { override def toString(): String = "lexer.CONSTANT" }

//lexer.IDENTIFIER: variable name - [a-zA-Z][a-zA-Z0-9]*
case class IDENTIFIER() extends TokenType { override def toString(): String = "lexer.IDENTIFIER" }

//lexer.COLON: ':'
case class COLON() extends TokenType { override def toString(): String = "lexer.COLON" }

//lexer.SEMICOLON: ';'
case class SEMICOLON() extends TokenType { override def toString(): String = "lexer.SEMICOLON" }

//lexer.EQUAL: '='
case class EQUAL() extends TokenType { override def toString(): String = "lexer.EQUAL" }

//lexer.NUMBERTYPE: 'number'
case class NUMBERTYPE() extends TokenType { override def toString(): String = "lexer.NUMBERTYPE" }

//lexer.STRINGTYPE: 'string'
case class STRINGTYPE() extends TokenType { override def toString(): String = "lexer.STRINGTYPE" }

//lexer.BOOLEANTYPE: 'boolean'
case class BOOLEANTYPE() extends TokenType { override def toString(): String = "lexer.BOOLEANTYPE" }

//lexer.LITERALSTRING: '["-']' + [a-zA-Z0-9]* + '["-']'
case class LITERALSTRING() extends TokenType {
  override def toString(): String = "lexer.LITERALSTRING"
}

//lexer.LITERALNUMBER: '['0-9]*'
case class LITERALNUMBER() extends TokenType {
  override def toString(): String = "lexer.LITERALNUMBER"
}

//lexer.LITERALBOOLEAN: 'true' | 'false'
case class LITERALBOOLEAN() extends TokenType {
  override def toString(): String = "lexer.LITERALBOOLEAN"
}

//lexer.SUM: '+'
case class SUM() extends TokenType { override def toString(): String = "lexer.SUM" }

//lexer.SUB: '-'
case class SUB() extends TokenType { override def toString(): String = "lexer.SUB" }

//lexer.MUL: '*'
case class MUL() extends TokenType { override def toString(): String = "lexer.MUL" }

//lexer.DIV: '/'
case class DIV() extends TokenType { override def toString(): String = "lexer.DIV" }

//lexer.IFCONDITION: 'if'
case class IF() extends TokenType { override def toString(): String = "lexer.IFCONDITION" }

//lexer.ELSECONDITION: 'else'
case class ELSE() extends TokenType {
  override def toString(): String = "lexer.ELSECONDITION"
}

//lexer.PRINTLN: 'println'
case class PRINTLN() extends TokenType { override def toString(): String = "lexer.PRINTLN" }

//lexer.READINPUT: 'readInput'
case class READINPUT() extends TokenType { override def toString(): String = "lexer.READINPUT" }

//lexer.LEFTPARENTHESIS: '('
case class LEFTPARENTHESIS() extends TokenType {
  override def toString(): String = "lexer.LEFTPARENTHESIS"
}

//lexer.RIGHTPARENTHESIS: ')'
case class RIGHTPARENTHESIS() extends TokenType {
  override def toString(): String = "lexer.RIGHTPARENTHESIS"
}

//lexer.LEFTBRACE: '{'
case class LEFTBRACE() extends TokenType { override def toString(): String = "lexer.LEFTBRACE" }

//lexer.RIGHTBRACE: '}'
case class RIGHTBRACE() extends TokenType { override def toString(): String = "lexer.RIGHTBRACE" }

//WHITESPACE: '\s'
//case class WHITESPACE() extends lexer.TokenType { override def toString() : String = "WHITESPACE" }
