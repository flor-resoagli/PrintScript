package org.florresoagli.printscript

import scala.collection.mutable
import scala.collection.mutable.Map

class Interpreter11Builder extends InterpreterBuilder {

  def build(): Interpreter = {

    val binaryOperators: Map[(BinaryOperator) => Boolean, (Double, Double) => Double] = Map(
      ((op: BinaryOperator) => op.isInstanceOf[PlusBinaryOperator]) -> ((a: Double, b: Double) =>
        a + b
      ),
      ((op: BinaryOperator) => op.isInstanceOf[MinusBinaryOperator]) -> ((a: Double, b: Double) =>
        a - b
      ),
      ((op: BinaryOperator) => op.isInstanceOf[MultiplyBinaryOperator]) -> (
        (a: Double, b: Double) => a * b
      ),
      ((op: BinaryOperator) => op.isInstanceOf[DivideBinaryOperator]) -> ((a: Double, b: Double) =>
        a / b
      )
    )

    val variableTypes: List[VariableType] =
      List[VariableType](
        new NumberVariableType(),
        new StringVariableType(),
        new BooleanVariableType(),
        new ConstantStringVariableType(),
        new ConstantNumberVariableType(),
        new ConstantBooleanVariableType()
      )

    val st: mutable.Map[String, (VariableType, Any)] = Map[String, (VariableType, Any)]()

    new Interpreter11(variableTypes, st, binaryOperators)
  }
}
