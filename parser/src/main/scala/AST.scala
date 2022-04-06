//  factor : INTEGER | LPAREN expr RPAREN

//exp: literal | var | op
//op: exp op exp | exp
//operator: + | - | * | /
//literal: String | Number

//trait or enum?
sealed trait VariableType
case class StringVariableType() extends VariableType
case class NumberVariableType() extends VariableType

sealed class BinaryOperator
case class PlusBinaryOperator() extends BinaryOperator
case class MinusBinaryOperator() extends BinaryOperator
case class MultiplyBinaryOperator() extends BinaryOperator
case class DivideBinaryOperator() extends BinaryOperator


sealed trait  AST
//case class DeclarationAssignationNode(variableTypeNode: VariableTypeNode, assignationNode: AssignationNode)
case class DeclarationAssignationNode(variable: Variable, variableTypeNode: VariableTypeNode, value: AST) extends AST
case class AssignationNode(variable: Variable, value: AST) extends AST
case class Variable(value: String) extends AST
case class ConstantNumb(value: Double) extends AST
case class ConstantString(value: String) extends AST
case class BinaryOperation(left: AST, operator: BinaryOperator, right: AST) extends AST
case class VariableTypeNode(value: VariableType) extends AST
case class PrintNode(value: AST) extends AST

