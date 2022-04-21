package org.florresoagli.printscript

import org.florresoagli.printscript.VariableType

import scala.collection.mutable.Map

trait RunningMode{
type InterpreterResult = (List[String], Map[String, (VariableType, Any)])
type PrintResult = List[String]

  def run(input: InterpreterResult): Unit
  def runError(message: List[String], stage: String): Unit
}

class ExecutionMode extends RunningMode{
  override def run(input: InterpreterResult): Unit = {
    println(s"Finished process")
    if(input._1.nonEmpty) println(s"Result: \n${input._1.mkString("\n")}")
  }
  override def runError(message: List[String], stage: String): Unit = {
    println("Failed to execute")
//    println(s"Error: ${message.mkString("")}")
  }
}

class ValidationMode extends RunningMode{
  override def run(input: InterpreterResult): Unit = {
    println(s"Validation succeeded, no syntax nor semantic errors found" )
  }
  override def runError(message: List[String], stage:String): Unit = {
    println(s"Validation failed at ${stage} stage, error messages: ${message}" )
  }

}
