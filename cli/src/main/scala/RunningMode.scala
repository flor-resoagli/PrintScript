trait RunningMode{
  def run(input: List[String]): Unit
//  def printError(message: List[String], stage: String): Unit = {
//    println(s"Validation failed during ${stage}, following errors found: ${message.mkString("\n")}")
//  }
}

class ExecutionMode extends RunningMode{
  override def run(input: List[String]): Unit = {
    println(s"Executing: $input")
  }
}

class ValidationMode extends RunningMode{
  override def run(input: List[String]): Unit = {
    println(s"Validation succeeded, no syntax nor semantic errors found" )
  }
}
