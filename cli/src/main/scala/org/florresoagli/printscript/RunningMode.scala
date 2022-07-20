package org.florresoagli.printscript

import org.florresoagli.printscript.VariableType
import java.util
import java.util.Observer
import scala.collection.mutable.Map
import scala.jdk.CollectionConverters._
import java.util.{List => JList}

trait Observer {

  def update(result: JList[String]):Unit
  def getList(): JList[String]

}
class PrintEmiterImpl extends Observer {

 val  output: JList[String] = new java.util.ArrayList[String]();

  def update(result: JList[String]): Unit = {
    output.addAll(result)
  }

  def getList(): JList[String] =  output

}

class ErrorEmitterImpl extends Observer {

  val  errors: JList[String] = new java.util.ArrayList[String]();

  def update(result: JList[String]) = {
    errors.addAll(result);
  }

  def getList(): JList[String] =  errors

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
    val (result, variables) = input
    printObserver.update(result.asJava)
    val outputMessage = if(result.nonEmpty) "Finished process with output: " else "Finished process, no output"
    println(s"${outputMessage} \n${result.mkString("\n")}")
    List(outputMessage) ++ result
  }
  override def runError(message: List[String], stage: String): List[String] = {
    errorObserver.update(message.asJava)
    val errorMessage = "Error in stage: " + stage + "\n" + message.mkString("\n")
    println(errorMessage)
    message
  }

}

class ValidationMode(printObserver: Observer, errorObserver: Observer) extends RunningMode{


  override def run(input: InterpreterResult): List[String] = {
    printObserver.update(input._1.asJava)
    val outputMessage = s"Validation succeeded, no syntax nor semantic errors found"
    println(outputMessage)
    List(outputMessage)

  }
  override def runError(message: List[String], stage:String): List[String] = {
    errorObserver.update(message.asJava)
    val errorMessage = s"Validation failed, errors found in $stage, error messages: "
    println(s"${errorMessage} ${message}" )
    List(errorMessage) ++ message
  }

}
