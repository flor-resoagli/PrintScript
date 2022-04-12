import junit.framework.TestCase
import org.junit.jupiter.api.{Test}
import org.junit.Assert.*

class InputReaderTesting {

  @Test
  def testReadFile(): Unit ={
    val inputReader = new FileReader("src/test/resources/test.txt")
    val actual = inputReader.read()

    val expected = "This is a test\nThis is another test\nThis is the last test"
    assertEquals(expected, actual)

  }

}
