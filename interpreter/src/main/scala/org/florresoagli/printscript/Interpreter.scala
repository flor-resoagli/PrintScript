package org.florresoagli.printscript

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}
import interpreter._


class Interpreter(val variableTypes: List[VariableType], val st: Map[String, (VariableType, Any)]) {

  var output = new ListBuffer[String]()
  var symbolTable = st

  def interpret(trees: List[AST]): (List[String], Map[String, (VariableType, Any)]) = {
    output.clear()
    trees.foreach(initialInterpretation)
    (output.toList, symbolTable)
  }

  def initialInterpretation(tree: AST): Unit = {
    tree match {
      case DeclarationAssignationNode(variable, variableType, value) => declareAssignVariable(variable, variableType, value)
      case AssignationNode(variable, value) => assignVariable(variable, value)
      case PrintNode(value) => printNode(value)
      case _ => throw new Exception(tree.getClass.getName + " is not a valid start operation")
    }
  }

  def declareAssignVariable(variable: Variable, variableType: VariableTypeNode, value: AST): Unit = {

    if(isExistingVariable(variable)){
      throw new Exception("Can't declare variable because it already exists")

    } else {

      symbolTable += (variable.value -> (variableType.value, None))
      assignVariable(variable, value)

    }
  }

  def isExistingVariable(variable: Variable): Boolean = {
    symbolTable.contains(variable.value)
  }

  def variableTypeMatches(variableType: VariableType, variableValue: Any): VariableType = {
    variableType.isInstance(variableValue) match {
      case true => variableType
      case false => throw new Exception("Can't assign " + variableValue + " to variable  because it's not of type ")
    }
  }

  def assignVariable(variable: Variable, value: AST): Unit = {

    if(isExistingVariable(variable)){
      val variableValue = interpretExpression(value)
      val variableTypeValue = variableTypeMatches(symbolTable(variable.value)._1, variableValue)
      symbolTable += (variable.value -> (variableTypeValue, variableValue))
    } else {
      throw new Exception("Can't assign variable because it doesn't exist")
    }

  }

  def interpretExpression(expression: AST): Any = {
    expression match {
      case c@Variable(_) => variableNode(c)
      case c@ConstantNumb(_) => constantNumbNode(c)
      case c@ConstantString(_) => constantStringNode(c)
      case c@BinaryOperation(_, _, _) => binaryOperationNode(c)
      case _ => throw new Exception("Can't interpret expression " + expression.getClass.getName)
    }
  }

  def variableNode(node:Variable): Any = {

    symbolTable.get(node.value) match {
      case Some((_, None)) => throw new Exception("Can't use variable " + node.value + " because it's not assigned")
      case Some((_, value)) => value
      case None => throw new Exception("Can't use variable " + node.value + " because it doesn't exist")
    }
  }

  def constantNumbNode(node:ConstantNumb): Double = node.value

  def constantStringNode(node:ConstantString): String = node.value

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
    operator match {
      case PlusBinaryOperator() => left + right
      case MinusBinaryOperator() => left - right
      case MultiplyBinaryOperator() => left * right
      case DivideBinaryOperator() => left / right
      case _ => throw new Exception("Can't perform operation " + operator + " with " + left + " and " + right)
    }
  }

  def concatString(left: String, right: String): String = {
    left + right
  }

  def printNode(node:AST): Unit = {
    val value = interpretExpression(node)
    output.append(value.toString)
  }

}