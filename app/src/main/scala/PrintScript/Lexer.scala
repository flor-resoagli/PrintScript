package PrintScript

import scala.collection.mutable.ListBuffer

class Lexer {

  def tokenize(input: String): ListBuffer[Token] = {
    var tokens = ListBuffer[Token]()
    var i = 0
    var word = ""
    var isString = false
    input.foreach {
      case c@( ' ' | '(' | ')' | '/' | '*' | '-' | '+' | '=' | ':' | ';' ) => {
        if(isString) word += c
        if (word != "" && !isString) {
          tokens.append(Token(matchword(word), i - word.length, i-1, word))
          word = ""
        }
        if(c != ' ') tokens.append(Token(matchchar(c), i, i, c.toString))
      }
      case c@('\'' | '\"') => {
        isString = !isString
        word += c
      }
      case c@_ => {
        word += c
      }
        i += 1
    }
    tokens
  }

  def matchchar(char: Char) : TokenType = {
    char match {
//        case ' ' => WHITESPACE.apply()
        case ':' => COLON.apply()
        case ';' => SEMICOLON.apply()
        case '=' => EQUAL.apply()
        case '+' => SUM.apply()
        case '-' => SUB.apply()
        case '*' => MUL.apply()
        case '/' => DIV.apply()
        case '(' => LEFTPARENTHESIS.apply()
        case ')' => RIGHTPARENTHESIS.apply()
        case _ => throw new Exception("Unknown character: " + char)
    }
  }

  def matchword(word: String): TokenType = {
    word match {
      case "let" => DECLARATION.apply()
      case "number" => NUMBERTYPE.apply()
      case "string" => STRINGTYPE.apply()
      case "println" => PRINTLN.apply()
      case w if w.matches("[a-zA-Z_][a-zA-Z0-9_]*") => IDENTIFIER.apply()
      case w if w.matches("[0-9.]+") => LITERALNUMBER.apply()
      case w if w.matches("[\"-\'][^_]*[\"-\']") => LITERALSTRING.apply()
    }
  }
}//a-zA-Z0-9 .,

object LexerApp extends App {
  val lexer = new Lexer
  val input = "let variable1: number = 5.25; let secondVariable = \"hello world! (This is a test string/set of chars)\"; let z = x + y; println(z);"
  val tokens = lexer.tokenize(input)
  tokens.foreach(println)
  println(input)
}