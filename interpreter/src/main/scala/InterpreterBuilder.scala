import scala.collection.mutable.Map

trait InterpreterBuilder {
  def build(): Interpreter
}

class DefaultInterpreterBuilder extends InterpreterBuilder {
  def build(): Interpreter = {
    val variableTypes: List[VariableType] = List[VariableType](
      new NumberVariableType(),
      new StringVariableType()
    )
    val symbolTable: Map[String, (VariableType, Any)] =  Map[String, (VariableType, Any)]()
    new Interpreter(variableTypes, symbolTable)
  }
}

