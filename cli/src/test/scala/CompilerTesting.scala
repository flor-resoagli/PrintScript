import junit.framework.TestCase
import org.junit.jupiter.api.Test
import org.junit.Assert.*
import org.florresoagli.printscript._

class CompilerTesting() {

  @Test
  def test01_printingStringOnExecutingModeShouldSucceed(): Unit = {
    val compiler =
      Compiler10Builder().build().compile("println(\"This is a string\");", new ExecutionMode())
    assertTrue(true)
  }

  @Test
  def test01_printingfromFileOnExecutingModeShouldSucceed(): Unit = {
    val compiler = Compiler10Builder()
      .build()
      .compile(new FileReader("src/test/resources/printString.txt").read(), new ExecutionMode())
    assertTrue(true)
  }
}
