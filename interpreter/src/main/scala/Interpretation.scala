object declarationAssignationInterpretation extends (AST => String) {
  def apply(ast: AST): String = {
    "declarationAssignationInterpretation"
  }
}













//object isInstanceOfDeclarationAssignationNode extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[DeclarationAssignationNode]
//}
//
//object isInstanceOfAssignationNode extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[AssignationNode]
//}
//
//object isInstanceOfVariableNode extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[Variable]
//}
//
//object isInstanceOfConstantNumb extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[ConstantNumb]
//}
//
//object isInstanceOfConstantString extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[ConstantString]
//}
//
//object isInstanceOfBinaryOperation extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[BinaryOperation]
//}
//
//object isInstanceOfVariableTypeNode extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[VariableTypeNode]
//}
//
//object isInstanceOfPrintNode extends (AST => Boolean) {
//  def apply(node: AST): Boolean = node.isInstanceOf[PrintNode]
//}
//



//case class DeclarationAssignationNode(variable: Variable, variableTypeNode: VariableTypeNode, value: AST) extends AST
//case class AssignationNode(variable: Variable, value: AST) extends AST
//case class Variable(value: String) extends AST
//case class ConstantNumb(value: Double) extends AST
//case class ConstantString(value: String) extends AST
//case class BinaryOperation(left: AST, operator: BinaryOperator, right: AST) extends AST
//case class VariableTypeNode(value: VariableType) extends AST
//case class PrintNode(value: AST) extends AST
//

