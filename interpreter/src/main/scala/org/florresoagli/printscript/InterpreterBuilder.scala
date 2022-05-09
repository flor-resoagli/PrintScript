package org.florresoagli.printscript

import java.io.File
import scala.collection.mutable.Map

class InterpreterBuilder {

  val symbolTable: Map[String, (VariableType, Any)] = Map[String, (VariableType, Any)]()

  val binaryOperators: Map[(BinaryOperator) => Boolean, (Double, Double) => Double] = Map(
    ((op: BinaryOperator) => op.isInstanceOf[PlusBinaryOperator]) -> ((a: Double, b: Double) =>
      a + b
    ),
    ((op: BinaryOperator) => op.isInstanceOf[MinusBinaryOperator]) -> ((a: Double, b: Double) =>
      a - b
    ),
    ((op: BinaryOperator) => op.isInstanceOf[MultiplyBinaryOperator]) -> ((a: Double, b: Double) =>
      a * b
    ),
    ((op: BinaryOperator) => op.isInstanceOf[DivideBinaryOperator]) -> ((a: Double, b: Double) =>
      a / b
    )
  )

  def build10(): Interpreter = {

    val variableTypes: List[VariableType] =
      List[VariableType](new NumberVariableType(), new StringVariableType())

    new Interpreter10(variableTypes, symbolTable, binaryOperators)

  }

  def build11(inputProvider: InputProviderReader): Interpreter = {

    val variableTypes: List[VariableType] =
      List[VariableType](
        new NumberVariableType(),
        new StringVariableType(),
        new BooleanVariableType(),
        new ConstantStringVariableType(),
        new ConstantNumberVariableType(),
        new ConstantBooleanVariableType()
      )

    new Interpreter11(variableTypes, symbolTable, binaryOperators, inputProvider)

  }

  def buildTesting11(testInputPath: String): Interpreter = {

    val variableTypes: List[VariableType] =
      List[VariableType](
        new NumberVariableType(),
        new StringVariableType(),
        new BooleanVariableType(),
        new ConstantStringVariableType(),
        new ConstantNumberVariableType(),
        new ConstantBooleanVariableType()
      )

    val reader = FakeIReader(testInputPath)

    new Interpreter11(variableTypes, symbolTable, binaryOperators, reader)
  }

}
