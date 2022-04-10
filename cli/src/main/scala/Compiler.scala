class Compiler(parser: Parser, lexer: Lexer, interpreter: InterpreterDummy)  {

  def compile(input: String, runningMode: RunningMode): Unit = {
    val tokens = lexer.tokenize(input)
    val ast = try parser.parseTokens(tokens) catch {
      case e: Exception => throw new Exception("Parsing error: " + e.getMessage)
    }
    val interpretedInput = interpreter.interpret(ast)
    runningMode.run(interpretedInput)
  }

}

trait CompilerBuilder() {
  def build(): Compiler
}

class DefaultCompilerBuilder extends CompilerBuilder {

  def build(): Compiler = {
    new Compiler(DefaultParser(), DefaultLexerBuilder().build(), DefaultInterpreterBuilderDummy().build())
  }


}





