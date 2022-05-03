package org.florresoagli.printscript

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

trait Interpreter(
                          val variableTypes: List[VariableType],
                          val st: Map[String, (VariableType, Any)],
                          val binaryOperations: Map[(BinaryOperator) => Boolean, (Double, Double) => Double],
                        ) {

  var output = new ListBuffer[String]()
  var symbolTable = st

  def interpret(trees: List[AST]): (List[String], Map[String, (VariableType, Any)]) = {
    output.clear()
    trees.foreach(initialInterpretation)
    (output.toList, symbolTable)
  }

  //FILL IN IN IMPLEMENTATION
  def initialInterpretation(tree: AST): Unit

  def isExistingVariable(variable: Variable): Boolean = {
    symbolTable.contains(variable.value)
  }

  def declareAssignVariable(variable: Variable, variableType: AST, value: AST): Unit = {

    if(isExistingVariable(variable)){
      throw new Exception("Can't declare variable because it already exists")

    } else {

      symbolTable += (variable.value -> (getVariableTypeValueFromAST(variableType), null))
      assignVariable(variable, value)

    }
  }

  def getVariableTypeValueFromAST(node: AST) : VariableType = {
    node match {
      case VariableTypeNode(value) => value
      case _ => throw new Exception("Can't get variable type from AST")
    }
  }

  //FILL IN IN IMPLEMENTATION
  def assignVariable(variable: Variable, value: AST): Unit

  def variableTypeMatches(variableType: VariableType, variableValue: Any): VariableType = {
    if(variableValue == null) return variableType
    variableType.isInstance(variableValue) match {
      case true => variableType
      case false => throw new Exception("Can't assign " + variableValue + " to variable  because it's not of type ")
    }
  }

  //FILL IN IN IMPLEMENTATION
  def interpretExpression(expression: AST): Any

  def variableNode(node:Variable): Any = {

    symbolTable.get(node.value) match {
      case Some((_, None)) => throw new Exception("Can't use variable " + node.value + " because it's not assigned")
      case Some((_, value)) => value
      case None => throw new Exception("Can't use variable " + node.value + " because it doesn't exist")
    }
  }

  def binaryOperationNode(node:BinaryOperation): Any = {
    val left = interpretExpression(node.left)
    val right = interpretExpression(node.right)

    (left, right, node.operator) match {
      case (left: Double, right: Double, operator: _) => binaryOperation(left, right, operator)
      case (left: String , right: _, operator: PlusBinaryOperator) => concatString(left, right.toString)
      case (left: _, right: String, operator: PlusBinaryOperator) => concatString(left.toString, right)
      case _ => throw new Exception("Can't perform binary operation " + node.operator + " with " + left + " and " + right)
    }
  }

  def binaryOperation(left: Double, right: Double, operator: BinaryOperator): Double = {
    binaryOperations.foreach(entry => {
      if(entry._1(operator)){
        return entry._2(left, right)
      }
    })
    throw new Exception("Can't perform binary operation " + operator + " with " + left + " and " + right)
  }

  def concatString(left: String, right: String): String = {
    left + right
  }

  def printNode(node: AST): Unit = {
    val value = interpretExpression(node)
    output.append(value.toString)
  }

}

