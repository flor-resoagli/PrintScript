package org.florresoagli.printscript

import java.util.Scanner
import scala.collection.mutable.Map

class Interpreter11(
  variableTypes: List[VariableType],
  symbolt: Map[String, (VariableType, Any)],
  binaryOperations: Map[(BinaryOperator) => Boolean, (Double, Double) => Double],
  reader: InputProviderReader
) extends Interpreter(variableTypes, symbolt, binaryOperations) {

  def initialInterpretation(tree: AST): Unit = {
    tree match {
      case DeclarationAssignationNode(variable, variableType, value) =>
        declareAssignVariable(variable, variableType, value)
      case AssignationNode(variable, value) => assignVariable(variable, value)
      case PrintNode(value)                 => printNode(value)
      case IfNode(cond, leftTrue, rightFalse) =>
        ifNodeInterpretation(cond.asInstanceOf[AST], leftTrue, rightFalse)
      case _ => throw new Exception(tree.getClass.getName + " is not a valid start operation")
    }
  }

  def assignVariable(variable: Variable, value: AST): Unit = {
    if (isExistingVariable(variable)) {
      val variableName = variable.value
      val (variableType, varValue) = st(variableName)
      if (variableType.isConstant() && varValue != null) {
        throw new Exception("Cannot assign a value to a constant variable")
      } else {
        val variableValue = interpretExpression(value)
        val variableTypeValue = variableTypeMatches(variableType, variableValue)
        symbolTable += (variable.value -> (variableTypeValue, variableValue))
      }
    } else {
      throw new Exception("Variable " + variable + " is not declared")
    }
  }

  def interpretExpression(expression: AST): Any = {
    expression match {
      case c @ Variable(_)              => variableNode(c)
      case c @ ConstantNumb(_)          => constantNumbNode(c)
      case c @ ConstantString(_)        => constantStringNode(c)
      case c @ ConstantBoolean(_)       => constantBooleanNode(c)
      case c @ BinaryOperation(_, _, _) => binaryOperationNode(c)
      case c @ ReadInputNode(_)         => readInputNode(c)
      case c @ EmptyNode()              => null
      case _ => throw new Exception("Can't interpret expression " + expression.getClass.getName)
    }
  }

  def readInputNode(node: ReadInputNode): String = {
    var message = ""
    node.message match {
      case ConstantString(value) => message = value
      case _ =>
        throw new Exception("Can't read input with message " + node.message.getClass.getName)
    }

    println(message)
    output.append(message)
    val input = reader.readSingleLine()
    input
  }

  def constantNumbNode(node: ConstantNumb): Double = node.value

  def constantStringNode(node: ConstantString): String = node.value

  def constantBooleanNode(node: ConstantBoolean): Boolean = node.value

  def ifNodeInterpretation(cond: AST, leftTrue: List[AST], rightFalse: List[AST]): Unit = {
    val condValue = interpretExpression(cond)
    if (condValue.asInstanceOf[Boolean]) {
      leftTrue.foreach(initialInterpretation(_))
    } else {
      rightFalse.foreach(initialInterpretation(_))
    }
  }

  def interpretCondition(cond: AST): Boolean = {
    cond match {
      case c @ Variable(_) => {
        val result = variableNode(c)
        if (result.isInstanceOf[Boolean]) {
          result.asInstanceOf[Boolean]
        } else {
          throw new Exception("Variable " + c + " is not a boolean")
        }
      }
      case c @ ConstantBoolean(_) => constantBooleanNode(c)
      case _ => throw new Exception("Can't interpret condition " + cond.getClass.getName)
    }
  }

}
