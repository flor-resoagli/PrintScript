import org.junit.Test
import org.junit.Assert._

class ParserTesting {

  val lexer = new DefaultLexerBuilder().build()
  val parser = new Parser2();

//  parser.parse(tokens)
//  assertTrue(true)

  @Test
  def test01_singleLiteralNumberShouldFail() = {
    val input = "1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test02_singleLiteralNumberShouldFailWithoutFinalSemiColon() = {
    val input = "variable = 3"
    val tokens = lexer.tokenize(input)

    val expected = AssignationNode("variable", ConstantNumb(3))

    assertTrue(false)



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

    val expected = DeclarationAssignationNode(Variable("a"),VariableTypeNode(NumberVariableType()),ConstantNumb(1.0))

    assertTrue(expected == parser.parse(tokens).get)

  }

  @Test
  def test05_DeclarationAssignationShouldFailWithoutLet() = {
    val input = " variable: number = 1;"
    val tokens = lexer.tokenize(input)


    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected = but found NUMBERTYPE"))

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

    assertTrue(thrown.getMessage.contains("Expected literal, variable or 'let' but found "))

  }
  @Test
  def test11_DeclarationAssignationShouldFailWithoutEquals() = {
    val input = "let variable: number 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected = but found "))
  }
  @Test
  def test12_SimpleAdditionDeclarationShouldSucceed() = {
    val input = "let variable: number = 1+1;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),PlusBinaryOperator(),ConstantNumb(1.0)))

    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test13_SimpleSubstractionDeclarationShouldSucceed() = {
    val input = "let variable: number = 1-1;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),MinusBinaryOperator(),ConstantNumb(1.0)))


    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test14_AssigningAPlusOperatorShouldFail() = {
    val input = "let variable: number = +;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected expression"))
  }

  @Test
  def test15_AssigningTwoConsecutivePlusOperatorsShouldFail() = {
    val input = "let variable: number = 2++;"

    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected expression"))
  }

  @Test
  def test16_singleSemicolonShouldntBeAccepted() = {
    val input = ";"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test17_doubleSemicolonAfterValidExceptionShouldntBeAccepted() = {
    val input = "let variable: number = 1+1;;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parse(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test18_SimpleMultiplicationShouldSucceed() = {
    val input = "let variable: number = 1*1;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(1.0)))


    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test18_SimpleDivisionShouldSucceed() = {
    val input = "let variable: number = 1/1;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),DivideBinaryOperator(),ConstantNumb(1.0)))


    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test19_NumberAddedToMultiplicationShouldSucceed() = {
    val input = "let variable: number = 2+1*2;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(2.0))))


    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test20_NumberAddedToDivisionShouldSucceed() = {
    val input = "let variable: number = 2+1/2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),BinaryOperation(ConstantNumb(1.0),DivideBinaryOperator(),ConstantNumb(2.0)))
    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation)


    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test21_MultiplicationAddedToNumberShouldSucceed() = {
    val input = "let variable: number = 1*2+2;"
    val tokens = lexer.tokenize(input)

    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(2.0)),PlusBinaryOperator(),ConstantNumb(2.0)))




    assertTrue(expected == parser.parse(tokens).get)
  }
  @Test
  def test22_DivisionAddedToNumberShouldSucceed() = {
    val input = "let variable: number = 2/1+2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(2.0),DivideBinaryOperator(),ConstantNumb(1.0)),PlusBinaryOperator(),ConstantNumb(2.0))
    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation)

    print(parser.parse(tokens).get)

//    assertTrue(expected == parser.parse(tokens).get)
  }

  @Test
  def test22_AdditioninPArenthesisAndThenMultipliedShouldSucceed() = {
    val input = "let variable: number = (2+1)*2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),ConstantNumb(1.0)),MultiplyBinaryOperator(),ConstantNumb(2.0))
    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation)

    print(parser.parse(tokens).get)

    //    assertTrue(expected == parser.parse(tokens).get)
  }





}