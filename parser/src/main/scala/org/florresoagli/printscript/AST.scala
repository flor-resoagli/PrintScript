package org.florresoagli.printscript

sealed trait VariableType {
  def isInstance(a: Any): Boolean
  def isConstant(): Boolean
}

case class StringVariableType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[String]
  def isConstant(): Boolean = false
}
case class NumberVariableType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[Double]
  def isConstant(): Boolean = false
}
case class ConstantStringType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[String]
  def isConstant(): Boolean = true
}
case class ConstantNumberType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[Double]
  def isConstant(): Boolean = true
}

//falta
case class BooleanVariableType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[Boolean]
  def isConstant(): Boolean = false
}

case class ConstantStringVariableType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[String]
  def isConstant(): Boolean = true
}
case class ConstantNumberVariableType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[Double]
  def isConstant(): Boolean = true
}

//falta
case class ConstantBooleanVariableType() extends VariableType {
  def isInstance(a: Any): Boolean = a.isInstanceOf[Boolean]
  def isConstant(): Boolean = true
}

sealed class BinaryOperator
case class PlusBinaryOperator() extends BinaryOperator
case class MinusBinaryOperator() extends BinaryOperator
case class MultiplyBinaryOperator() extends BinaryOperator
case class DivideBinaryOperator() extends BinaryOperator

sealed trait AST { def isEmpty(): Boolean = { this.isInstanceOf[EmptyNode] } }

//case class NewDeclarationAssignationNode(variableTypeNode: VariableTypeNode, assignationNode: AssignationNode) extends AST
case class DeclarationAssignationNode(variable: Variable, variableTypeNode: AST, value: AST)
    extends AST

case class AssignationNode(variable: Variable, value: AST) extends AST

case class Variable(value: String) extends AST

case class ConstantNumb(value: Double) extends AST

case class ConstantString(value: String) extends AST

case class ConstantBoolean(value: Boolean) extends AST

case class BinaryOperation(left: AST, operator: BinaryOperator, right: AST) extends AST

case class VariableTypeNode(value: VariableType) extends AST

case class PrintNode(value: AST) extends AST

case class EmptyNode() extends AST

case class IfNode(cond: AST, leftTrue: List[AST], rightFalse: List[AST]) extends AST

case class ReadInputNode(message: AST) extends AST
