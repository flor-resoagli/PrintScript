import scala.io.StdIn.readLine
import org.florresoagli.printscript.{
  CompilerRunner,
  ConsoleIReader,
  ConsoleReader,
  ErrorEmitterImpl,
  ExecutionMode,
  FileReader,
  InputReader,
  PrintEmiterImpl,
  RunningMode,
  ValidationMode
}

import java.util.Observer

val printEmitter = new PrintEmiterImpl()
val errorEmitter = new ErrorEmitterImpl()

object CliRunner extends App {

  // stop program
  while (true) {
    UIRun()
    checkIfEnd()
    println(printEmitter.getList())
    println(errorEmitter.getList())

  }
}

private def checkIfEnd() = {
  println("Do you want to end the program? (y/n)")
  val input = readLine()
  if (input == "y") stop()
}

private def stop(): Unit = {
  println("Goodbye!")
  System.exit(0)
}
private def UIRun() = {

  val input: InputReader = askForInput
  val version: String = askForVersion
  val runningMode: RunningMode = askForRunningMode
  tryToCompile(input, version, runningMode)

}

private def chooseMode(): RunningMode = {
  val input = readLine()
  input match {
    case "1" => {
      println("Running in validation mode")
      new ValidationMode(printEmitter, errorEmitter)
    }
    case "2" => {
      println("Running in execution mode")
      new ExecutionMode(printEmitter, errorEmitter)
    }
    case _ => {
      println("Unsupported mode")
      return null
    }
  }
}
private def chooseVersion(): String = {
  val version = readLine()
  version match {
    case "1" => "1.0"
    case "2" => "1.1"
  }
}
private def chooseInput(): InputReader = {
  val input = readLine()
  input match {
    case "1" => {
      println("Enter file path: ")
      val filePath = readLine()
      new FileReader(filePath)
    }
    case "2" => {
      new ConsoleReader()
    }
    case _ => {
      println("Wrong input")
      chooseInput()
    }
  }
}

def askForInput: InputReader = {
  println("Welcome!\n  Choose input type: \n  1. File \n  2. Console")
  val input = chooseInput()
  input
}

def askForVersion: String = {
  println("Choose compiler version: \n  1. 1.0 \n 2. 1.1")
  val version = chooseVersion()
  version
}
def askForRunningMode: RunningMode = {
  println("Choose mode: \n  1. Validation \n  2. Execution")
  val runningMode = chooseMode()
  runningMode
}

def tryToCompile(input: InputReader, version: String, runningMode: RunningMode): Unit = {
  val inputProvider: ConsoleIReader = ConsoleIReader()
  val result =
    try new CompilerRunner().run(input, version, runningMode, inputProvider)
    catch {
      case e: Exception => {
        println(s"${e.getMessage}, please run again")
      }
    }
}
