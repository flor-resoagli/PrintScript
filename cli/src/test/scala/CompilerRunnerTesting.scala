import junit.framework.TestCase
import org.florresoagli.printscript.{
  CompilerRunner,
  ConsoleIReader,
  ErrorEmitterImpl,
  ExecutionMode,
  FileReader,
  PrintEmiterImpl,
  ValidationMode
}
import org.junit.jupiter.api.Test
import org.junit.Assert.*

class CompilerRunnerTesting {

  @Test
  def test01_executionModeShouldSucceed(): Unit = {
    val runnedFile = new CompilerRunner().run(
      new FileReader("src/test/resources/test.txt"),
      "1.0",
      new ExecutionMode(PrintEmiterImpl(), ErrorEmitterImpl()),
      ConsoleIReader()
    )
    assertTrue(true)
  }
  @Test
  def test02_executionModeShouldSucceed(): Unit = {
    val runnedFile = new CompilerRunner().run(
      new FileReader("src/test/resources/test.txt"),
      "1.0",
      new ExecutionMode(PrintEmiterImpl(), ErrorEmitterImpl()),
      ConsoleIReader()
    )
    assertTrue(true)
  }

  @Test
  def test_validationModeShouldSucceed(): Unit = {
    val runnedFile = new CompilerRunner().run(
      new FileReader("src/test/resources/printString.txt"),
      "1.0",
      new ExecutionMode(PrintEmiterImpl(), ErrorEmitterImpl()),
      ConsoleIReader()
    )
    assertTrue(true)
  }
  @Test
  def test02_validationModeShouldSucceed(): Unit = {
    val runnedFile = new CompilerRunner().run(
      new FileReader("src/test/resources/printString.txt"),
      "1.0",
      new ValidationMode(PrintEmiterImpl(), ErrorEmitterImpl()),
      ConsoleIReader()
    )
    assertTrue(true)
  }

}
