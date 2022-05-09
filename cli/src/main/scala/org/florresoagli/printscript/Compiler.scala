package org.florresoagli.printscript

import org.florresoagli.printscript.{
  AST,
  Interpreter,
  Lexer,
  Parser,
  Token,
  VariableType,
  RunningMode
}

import scala.collection.mutable.Map

class Compiler(parser: Parser, lexer: Lexer, interpreter: Interpreter) {
  type InterprterResult = (List[String], Map[String, (VariableType, Any)])

  def compile(input: String, runningMode: RunningMode): Unit = {
    val tokens = tryToLex(lexer, runningMode, input, "lexing") match {
      case Some(value) => value
      case None        => return
    }
    val ast: List[AST] = tryToParse(runningMode, tokens, "parsing") match {
      case Some(result) => result
      case None         => return
    }
    val interpretedInput: InterprterResult =
      tryToInterpret(runningMode, "interpreting", interpreter.interpret(ast)) match {
        case Some(result) => result
        case None         => return
      }

    runningMode.run(interpretedInput)
  }

  def tryToLex(
    lexer: Lexer,
    runningMode: RunningMode,
    input: String,
    stage: String
  ): Option[List[Token]] = {
    val tokenList =
      try lexer.tokenize(input)
      catch {
        case e: Exception => {
          runningMode.runError(List(e.getMessage), stage)
          return None
        }
      }
    Some(tokenList)
  }

  private def tryToParse(
    runningMode: RunningMode,
    tokens: List[Token],
    stage: String
  ): Option[List[AST]] = {
    printStage(stage)
    val ast: List[AST] =
      try parser.parseTokens(tokens)
      catch {
        case e: Exception => {
          runningMode.runError(List(e.getMessage), stage)
          return None
        }
      }
    Some(ast)
  }

  private def printStage(stage: String) = {
    println(s"Stage: $stage ...")
  }

  private def tryToInterpret(
    runningMode: RunningMode,
    stage: String,
    func: => InterprterResult
  ): Option[InterprterResult] = {
    printStage(stage)
    val ast: InterprterResult =
      try func
      catch {
        case e: Exception => {
          runningMode.runError(List(e.getMessage), stage)
          return None
        }
      }
    Some(ast)
  }

}

trait CompilerBuilder() {
  def build(): Compiler
}

class Compiler10Builder extends CompilerBuilder {

  def build(): Compiler = {
    new Compiler(Parser10().build(), Lexer10Builder().build(), InterpreterBuilder().build10())
  }

}

class Compiler11Builder extends CompilerBuilder {

  def build(): Compiler = {
    new Compiler(
      Parser11().build(),
      Lexer11Builder().build(),
      InterpreterBuilder().build11(ConsoleIReader())
    )
  }

  def build11(inputProvider: InputProviderReader): Compiler = {
    new Compiler(
      Parser11().build(),
      Lexer11Builder().build(),
      InterpreterBuilder().build11(inputProvider)
    )
  }
}

class TestingCompiler11Builder extends CompilerBuilder {

  def build(): Compiler = {
    new Compiler(
      Parser11().build(),
      Lexer11Builder().build(),
      InterpreterBuilder().buildTesting11("input.txt")
    )
  }
}
