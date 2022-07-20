package org.florresoagli.printscript

import scala.collection.mutable.ListBuffer

class Lexer(tokenConditions: List[TokenCondition]) {

  private var lexStates = List[LexicalState]()

  private case class LexicalState(column: Int, line: Int)

  def tokenize(input: String): List[Token] = {
    val trimmedInput = input.trim
    lexStates = lexicalStateDeclaration(trimmedInput)
    buildTokens(trimmedInput)
  }

  private def lexicalStateDeclaration(input: String): List[LexicalState] = {
    var line   = 0
    var column = -1
    input
      .map(c => {
        if (isNewLine(c)) {
          line += 1
          column = 0
        } else {
          column += 1
        }
        LexicalState(column, line)
      })
      .toList
  }
  private def isNewLine(c: Char) = c == '\n'

  private def buildTokens(input: String): List[Token] = {
    var position = 0
    var tokens   = ListBuffer[Token]()

    while (position < input.length) {
      var found = false
      while ((input(position) == ' ' || input(position) == '\n') && position < input.length - 1) {
        position += 1
      }
      for (tokenCondition <- tokenConditions) {
        if (!found && input(position) != ' ') {
          tokenCondition.apply(position, input) match {
            case Some((finalPosition, tokenType)) => {
              tokens.append(
                Token(
                  tokenType,
                  AbsoluteRange(position, finalPosition),
                  lexicalRange(position, finalPosition),
                  input.substring(position, finalPosition + 1)
                )
              )
              position = finalPosition + 1
              found = true
            }
            case None => {}
          }
        }
      }
      if (!found) { throw new Exception("Unrecognized symbol: " + input(position)) }
    }
    tokens.toList
  }

  private def lexicalRange(from: Int, to: Int): LexicalRange = {
    LexicalRange(
      lexStates(from).column,
      lexStates(to).column,
      lexStates(from).line,
      lexStates(to).line
    )
  }

}
