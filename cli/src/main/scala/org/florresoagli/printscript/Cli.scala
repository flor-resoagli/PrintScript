package org.florresoagli.printscript

class Cli() {

  def run(inputReader: InputReader, version: String, mode: RunningMode): Any =
    version match {
      case "1.0" => DefaultCompilerBuilder().build().compile(inputReader.read(), mode)
      case _ => throw new Exception("Version not supported")
    }
}







