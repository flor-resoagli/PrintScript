import org.junit.Test
import org.junit.Assert._

class ParserTesting {

  val lexerBuilder = new DefaultLexerBuilder().build()
  val parser = new Parser();

  @Test
  def test01_singleLiteralNumberShouldSuceed() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "1;"
    val tokens = lexer.tokenize(input)

    val expected = ConstantNumb(1);
    val actual = parser.parse(tokens)
    println(actual)
    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test02_singleLiteralNumberShouldFailWithoutFinalSemiColon() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "1"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains("Line should end with semicolon"))

  }
  @Test
  def test03_shouldSucceedWithEmptyInput() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = ""
    val tokens = lexer.tokenize(input)


    val expected = Option.empty[AST]
    assertTrue(expected == parser.parse(tokens))
  }


  @Test
  def test04_DeclarationAssignationShouldSucceed() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "let a: number = 1;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("a"),VariableTypeNode(NumberTypee()),ConstantNumb(1.0))


    assertTrue(expected == parser.parse(tokens).get)

  }

  @Test
  def test05_DeclarationAssignationShouldFailWithoutLet() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = " variable: number = 1;"
    val tokens = lexer.tokenize(input)
//
//    val expected =
//


  }
  @Test
  def test06_DeclarationAssignationShouldFailWithoutTypeAssignationSemicolon() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "let variable number = 1;"
    val tokens = lexer.tokenize(input)

//    val expected =
//
//    val actual = parser.parse(tokens)


  }
  @Test
  def test07_DeclarationAssignationShouldFailWithoutTypeAssignation() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = " variable = 1;"
    val tokens = lexer.tokenize(input)
//
//    val expected =
//
//    val actual = parser.parse(tokens)

  }

  @Test
  def test08_DeclarationAssignationShouldFailWithoutValue() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "let variable: number = ;"
    val tokens = lexer.tokenize(input)
//
//    val expected =
//
//    val actual = parser.parse(tokens)

  }
  @Test
  def test09_DeclarationAssignationShouldFailWithoutFinalSemicolon() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "let variable: number = 1"
    val tokens = lexer.tokenize(input)
    //
    //    val expected =
    //
    //    val actual = parser.parse(tokens)

  }

  @Test
  def test10_singleSemicolonShoudFail() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = ";"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains("Expected EOF"))

  }





}