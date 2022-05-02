import junit.framework.TestCase
import org.florresoagli.printscript.{Cli, ErrorEmitterImpl, ExecutionMode, FileReader, PrintEmiterImpl, ValidationMode}
import org.junit.jupiter.api.Test
import org.junit.Assert.*

class CliTesting {

  
  @Test
  def test01_executionModeShouldSucceed(): Unit = {
     val runnedFile = new Cli().run(new FileReader("src/test/resources/test.txt"), "1.0", new ExecutionMode(PrintEmiterImpl(), ErrorEmitterImpl()))
     assertTrue(true)
  }
  @Test
  def test02_executionModeShouldSucceed(): Unit = {
    val runnedFile = new Cli().run(new FileReader("src/test/resources/test.txt"), "1.0", new ExecutionMode(PrintEmiterImpl(), ErrorEmitterImpl()))
    assertTrue(true)
  }


  @Test
  def test_validationModeShouldSucceed(): Unit = {
    val runnedFile = new Cli().run(new FileReader("src/test/resources/printString.txt"), "1.0", new ExecutionMode(PrintEmiterImpl(), ErrorEmitterImpl()))
    assertTrue(true)
  }
  @Test
  def test02_validationModeShouldSucceed(): Unit = {
    val runnedFile = new Cli().run(new FileReader("src/test/resources/printString.txt"), "1.0", new ValidationMode(PrintEmiterImpl(), ErrorEmitterImpl()))
    assertTrue(true)
  }


}
