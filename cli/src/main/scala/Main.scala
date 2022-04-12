import scala.io.StdIn.readLine

object CliRunner extends App {

  //stop program
  while (true) {
    UIRun()
    checkIfEnd()
  }
}

private def checkIfEnd() ={
  println("Do you want to end the program? (y/n)")
  val input = readLine()
  if(input == "y") stop()
}

private def stop(): Unit = {
  println("Goodbye!")
  System.exit(0)
}
private def UIRun() = {
  println("Welcome!\n  Choose input type: \n  1. File \n  2. Console")
  val input = chooseInput()
  println("Choose compiler version: \n  1. 1.0")
  val version = chooseVersion()
  println("Choose mode: \n  1. Validation \n  2. Execution")
  val runningMode = chooseMode()
  val result = new Cli().run(input, version, runningMode)
}

private def chooseMode(): RunningMode ={
  val input = readLine()
  input match {
    case "1" => {
      println("Running in validation mode")
      new ValidationMode()
    }
      case "2" => {
        println("Running in execution mode")
        new ExecutionMode()
      }
    case _ => {
      println("Unsupported mode")
      return null
    }
  }
}
private def  chooseVersion(): String = {
  val version = readLine()
  version match {
    case "1" => "1.0"
  }
}
private def chooseInput(): InputReader ={
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