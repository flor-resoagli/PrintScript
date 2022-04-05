import org.junit.Test
import org.junit.Assert._

class ParserTesting {

  val lexer = new DefaultLexerBuilder().build()
  val parser = new Parser();

  @Test
  def test01_singleLiteralNumberShouldSuceed() = {
    val input = "1;"
    val tokens = lexer.tokenize(input)

    val expected = ConstantNumb(1);
    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test02_singleLiteralNumberShouldFailWithoutFinalSemiColon() = {
    val input = "1"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains("Line should end with semicolon"))

  }
  @Test
  def test03_shouldSucceedWithEmptyInput() = {
    val input = ""
    val tokens = lexer.tokenize(input)


    val expected = Option.empty[AST]
    assertTrue(expected == parser.parse(tokens))
  }


  @Test
  def test04_DeclarationAssignationShouldSucceed() = {
    val input = "let a: number = 1;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("a"),VariableTypeNode(NumberTypee()),ConstantNumb(1.0))


    assertTrue(expected == parser.parse(tokens).get)

  }

  @Test
  def test05_DeclarationAssignationShouldFailWithoutLet() = {
    val input = " variable: number = 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected literal, variable or 'let' but found "))

  }
  @Test
  def test06_DeclarationAssignationShouldFailWithoutTypeAssignationColon() = {
    val input = "let variable number = 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected : but found "))
  }

  @Test
  def test07_DeclarationAssignationShouldFailWithoutTypeAssignation() = {
    val input = "let variable: = 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected variable type but found "))

  }

  @Test
  def test08_DeclarationAssignationShouldFailWithoutValue() = {
    val input = "let variable: number = ;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s""))

  }
  @Test
  def test09_DeclarationAssignationShouldFailWithoutFinalSemicolon() = {
    val input = "let variable: number = 1"

    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Line should end with semicolon"))

  }

  @Test
  def test10_singleSemicolonShoudFail() = {
    val input = ";"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains("Expected EOF"))

  }
  @Test
  def test11_DeclarationAssignationShouldFailWithoutEquals() = {
    val input = "let variable: number 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected = but found "))
  }





}