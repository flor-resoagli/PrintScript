package org.florresoagli.printscript

import org.florresoagli.printscript.VariableType
import java.util
import java.util.Observer
import scala.collection.mutable.Map
import scala.jdk.CollectionConverters._



trait Observer {

  def update(result: java.util.List[java.lang.String]):Unit
  def getList(): java.util.List[java.lang.String]

}
class PrintEmiterImpl extends Observer {

 val  output: java.util.List[java.lang.String] = new java.util.ArrayList[java.lang.String]();

  def update(result: java.util.List[java.lang.String]) = {
    output.addAll(result)
  }

  def getList(): java.util.List[java.lang.String] =  {
    return output
  }
}


class ErrorEmitterImpl extends Observer {

  val  errors: java.util.List[java.lang.String] = new java.util.ArrayList[java.lang.String]();

  def update(result: java.util.List[java.lang.String]) = {
    errors.addAll(result);
  }

  def getList(): java.util.List[java.lang.String] =  {
    return errors
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
    printObserver.update(input._1.asJava)
    val outputMessage = if(input._1.nonEmpty) "Finished process with output: " else "Finished process, no output"
    println(s"${outputMessage} \n${input._1.mkString("\n")}")
    List(outputMessage) ++ input._1
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