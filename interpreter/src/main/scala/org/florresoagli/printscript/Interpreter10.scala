package org.florresoagli.printscript

import scala.collection.mutable.Map

class Interpreter10(
  variableTypes: List[VariableType],
  st: Map[String, (VariableType, Any)],
  binaryOperations: Map[(BinaryOperator) => Boolean, (Double, Double) => Double]
) extends Interpreter(variableTypes, st, binaryOperations) {

  def initialInterpretation(tree: AST): Unit = {
    tree match {
      case DeclarationAssignationNode(variable, variableType, value) =>
        declareAssignVariable(variable, variableType, value)
      case AssignationNode(variable, value) => assignVariable(variable, value)
      case PrintNode(value)                 => printNode(value)
      case _ => throw new Exception(tree.getClass.getName + " is not a valid start operation")
    }
  }

  def assignVariable(variable: Variable, value: AST): Unit = {

    if (isExistingVariable(variable)) {
      val variableValue = interpretExpression(value)
      val variableTypeValue = variableTypeMatches(symbolTable(variable.value)._1, variableValue)
      symbolTable += (variable.value -> (variableTypeValue, variableValue))
    } else {
      throw new Exception("Can't assign variable because it doesn't exist")
    }

  }

  def interpretExpression(expression: AST): Any = {
    expression match {
      case c @ Variable(_)              => variableNode(c)
      case c @ ConstantNumb(_)          => constantNumbNode(c)
      case c @ ConstantString(_)        => constantStringNode(c)
      case c @ BinaryOperation(_, _, _) => binaryOperationNode(c)
      case _ => throw new Exception("Can't interpret expression " + expression.getClass.getName)
    }
  }

  def constantNumbNode(node: ConstantNumb): Double = node.value

  def constantStringNode(node: ConstantString): String = node.value

}
