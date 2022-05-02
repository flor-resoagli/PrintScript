package org.florresoagli.printscript

import org.florresoagli.printscript.VariableType

import scala.collection.mutable.Map

trait Observer{

  def update(result: List[String]): Unit

}

trait RunningMode{
type InterpreterResult = (List[String], Map[String, (VariableType, Any)])
type PrintResult = List[String]

  val observers: List[Observer]
  def run(input: InterpreterResult): List[String]
  def runError(message: List[String], stage: String): List[String]
  def addObserver(observer: Observer): RunningMode
}

class ExecutionMode(observersList: List[Observer]) extends RunningMode{

  val observers: List[Observer] = observersList

  override def run(input: InterpreterResult): List[String] = {
    println(s"Finished process")
    observers.foreach(observer => observer.update(input._1))
    val outputMessage = if(input._1.nonEmpty) "Finished process with output: " else "Finished process, no output"
    println(s"${outputMessage} \n${input._1.mkString("\n")}")
    List(outputMessage) ++ input._1
  }
  override def runError(message: List[String], stage: String): List[String] = {
    observers.foreach(observer => observer.update(message))
//    val errorMessage = "Error in stage: " + stage + "\n" + message.mkString("\n")
    println("Failed to execute")
    message
  }
  override def addObserver(observer: Observer): RunningMode = {
    new ExecutionMode(observer :: observers)
  }
}

class ValidationMode(observersList: List[Observer]) extends RunningMode{

  val observers: List[Observer] = observersList

  override def run(input: InterpreterResult): List[String] = {
    observers.foreach(observer => observer.update(input._1))
    val outputMessage = s"Validation succeeded, no syntax nor semantic errors found"
    println(outputMessage)
    List(outputMessage)

  }
  override def runError(message: List[String], stage:String): List[String] = {
    observers.foreach(observer => observer.update(message))
    val errorMessage = s"Validation failed, errors found in $stage, error messages: "
    println(s"${errorMessage} ${message}" )
    List(errorMessage) ++ message
  }
  override def addObserver(observer: Observer): RunningMode = {
    new ValidationMode(observer :: observers)
  }

}
