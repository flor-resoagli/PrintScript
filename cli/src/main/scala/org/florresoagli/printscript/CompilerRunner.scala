package org.florresoagli.printscript

class CompilerRunner() {

  def run(
    inputReader: InputReader,
    version: String,
    mode: RunningMode,
    inputProvider: InputProviderReader
  ): Any =
    version match {
      case "1.0"  => Compiler10Builder().build().compile(inputReader.read(), mode)
      case "1.1"  => Compiler11Builder().build11(inputProvider).compile(inputReader.read(), mode)
      case "t1.1" => TestingCompiler11Builder().build().compile(inputReader.read(), mode)
      case _      => throw new Exception("Version not supported")
    }
}
