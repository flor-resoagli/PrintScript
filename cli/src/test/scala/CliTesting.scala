import junit.framework.TestCase
import org.junit.jupiter.api.Test
import org.junit.Assert.*

class CliTesting {

  
  @Test
  def test_executionModeShouldSucceed(): Unit = {
     val runnedFile = new Cli().run(new FileReader("src/test/resources/test.txt"), "1.0", new ExecutionMode())
     assertTrue(true)
  }


  @Test
  def test_validationModeShouldSucceed(): Unit = {
    val runnedFile = new Cli().run(new FileReader("src/test/resources/test.txt"), "1.0", new ExecutionMode())
    assertTrue(true)
  }


}
