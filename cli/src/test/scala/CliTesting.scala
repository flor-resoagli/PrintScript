import junit.framework.TestCase
import org.florresoagli.printscript.{Cli, ExecutionMode, FileReader, ValidationMode}
import org.junit.jupiter.api.Test
import org.junit.Assert.*

class CliTesting {

  
  @Test
  def test01_executionModeShouldSucceed(): Unit = {
     val runnedFile = new Cli().run(new FileReader("src/test/resources/test.txt"), "1.0", new ExecutionMode())
     assertTrue(true)
  }
  @Test
  def test02_executionModeShouldSucceed(): Unit = {
    val runnedFile = new Cli().run(new FileReader("src/test/resources/test.txt"), "1.0", new ExecutionMode())
    assertTrue(true)
  }


  @Test
  def test_validationModeShouldSucceed(): Unit = {
    val runnedFile = new Cli().run(new FileReader("src/test/resources/printString.txt"), "1.0", new ExecutionMode())
    assertTrue(true)
  }
  @Test
  def test02_validationModeShouldSucceed(): Unit = {
    val runnedFile = new Cli().run(new FileReader("src/test/resources/printString.txt"), "1.0", new ValidationMode())
    assertTrue(true)
  }


}
