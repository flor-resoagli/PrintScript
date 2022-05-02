package org.florresoagli.printscript

import org.florresoagli.printscript.VariableType

import java.util.Observer
import scala.collection.mutable.Map

trait Observer {
  def update(result: List[String]): Unit
}
case class PrintEmiterImpl() extends Observer {

  def update(result: List[String]): Unit = {
    result.foreach(println)
  }
}


case class ErrorEmitterImpl() extends Observer {

  private var errors: List[String] = List()

  def update(result: List[String]): Unit = {
    errors = errors ++ result
  }

  def getErrors(): List[String] = {
    errors
  }
}




trait RunningMode{
type InterpreterResult = (List[String], Map[String, (VariableType, Any)])
type PrintResult = List[String]

  def run(input: InterpreterResult): List[String]
  def runError(message: List[String], stage: String): List[String]
}

class ExecutionMode(printObserver: Observer, errorObserver: Observer) extends RunningMode{


  override def run(input: InterpreterResult): List[String] = {
    println(s"Finished process")
    printObserver.update(input._1)
    val outputMessage = if(input._1.nonEmpty) "Finished process with output: " else "Finished process, no output"
    println(s"${outputMessage} \n${input._1.mkString("\n")}")
    List(outputMessage) ++ input._1
  }
  override def runError(message: List[String], stage: String): List[String] = {
    errorObserver.update(message)
    val errorMessage = "Error in stage: " + stage + "\n" + message.mkString("\n")
    println(errorMessage)
    message
  }

}

class ValidationMode(printObserver: Observer, errorObserver: Observer) extends RunningMode{


  override def run(input: InterpreterResult): List[String] = {
    printObserver.update(input._1)
    val outputMessage = s"Validation succeeded, no syntax nor semantic errors found"
    println(outputMessage)
    List(outputMessage)

  }
  override def runError(message: List[String], stage:String): List[String] = {
    errorObserver.update(message)
    val errorMessage = s"Validation failed, errors found in $stage, error messages: "
    println(s"${errorMessage} ${message}" )
    List(errorMessage) ++ message
  }

}
