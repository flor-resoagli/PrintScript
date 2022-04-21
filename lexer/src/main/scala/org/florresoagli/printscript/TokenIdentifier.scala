package org.florresoagli.printscript

trait TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)]

  def endOfWord(position: Int, input: String): Int = {
    var p = position
    while (p < input.length && isCharacter(input(p))) {
      p = p + 1
    }
    p - 1
  }

  private def isCharacter(c: Char): Boolean = c.toString.matches("[A-Za-z0-9_.]")
}

case class ColonCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == ':') Some(position, COLON.apply()) else None
}

case class SemicolonCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == ';') Some(position, SEMICOLON.apply()) else None
}

case class EqualCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '=') Some(position, EQUAL.apply()) else None
}

case class SumCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '+') Some(position, SUM.apply()) else None
}

case class SubCondition() extends TokenCondition {
  // does not apply for negative numbers
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '-') Some(position, SUB.apply()) else None
}

case class MulCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '*') Some(position, MUL.apply()) else None
}

case class DivCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '/') Some(position, DIV.apply()) else None
}

case class LeftParenthesisCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '(') Some(position, LEFTPARENTHESIS.apply()) else None
}

case class RightParenthesisCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == ')') Some(position, RIGHTPARENTHESIS.apply()) else None
}

case class LeftBraceCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '{') Some(position, LEFTBRACE.apply()) else None
}

case class RightBraceCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    if (input(position) == '}') Some(position, RIGHTBRACE.apply()) else None
}

case class DeclarationCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "let") Some(eow, DECLARATION.apply()) else None
  }
}

case class ConstantCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "const") Some(eow, CONSTANT.apply()) else None
  }
}

case class NumberTypeCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "number") Some(eow, NUMBERTYPE.apply()) else None
  }
}

case class StringTypeCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "string") Some(eow, STRINGTYPE.apply()) else None
  }
}

case class BooleanTypeCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "boolean") Some(eow, BOOLEANTYPE.apply()) else None
  }
}

case class IfCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "if") Some(eow, IF.apply()) else None
  }
}

case class ElseCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "else") Some(eow, ELSE.apply()) else None
  }
}

case class PrintlnCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "println") Some(eow, PRINTLN.apply()) else None
  }
}

case class ReadInputCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1) == "readInput") Some(eow, READINPUT.apply()) else None
  }
}

case class IdentifierCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1).matches("[a-zA-Z_][a-zA-Z0-9_]*"))
      Some(eow, IDENTIFIER.apply())
    else None
  }
}

case class LiteralBooleanCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (
      input.substring(position, eow + 1) == "true" || input.substring(position, eow + 1) == "false"
    )
      Some(eow, LITERALBOOLEAN.apply())
    else None
  }
}

case class LiteralNumberCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] = {
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1).matches("-?[0-9.,]+")) Some(eow, LITERALNUMBER.apply())
    else None
  }
}

case class LiteralStringCondition() extends TokenCondition {
  def apply(position: Int, input: String): Option[(Int, TokenType)] =
    val eow = endOfWord(position, input)
    if (input.substring(position, eow + 1).matches("[\"-\'][^_]*[\"-\']"))
      Some(eow, LITERALSTRING.apply())
    else None

  override def endOfWord(position: Int, input: String): Int = {
    var eow = position + 1
    while (input(eow) != '\"' && input(eow) != '\'') eow = eow + 1
    eow
  }

}
