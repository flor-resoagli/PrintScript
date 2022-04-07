import junit.framework.TestCase
import org.junit.jupiter.api.{Test}
import org.junit.Assert.*

class ParserTesting {

  val lexer = new DefaultLexerBuilder().build()
  val parser = new Parser();



  @Test
  def test01_singleLiteralNumberShouldFail() = {
    val input = "1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }
//
  @Test
  def test02_variableAssignationShouldSuceed() = {
    val input = "variable = 3;"
    val tokens = lexer.tokenize(input)

    val expected = List(AssignationNode(Variable("variable"), ConstantNumb(3)))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test03_shouldSucceedWithEmptyInput() = {
    val input = ""
    val tokens = lexer.tokenize(input)


    val expected = List().empty
    assertEquals(expected, parser.parseTokens(tokens))
  }

//

  @Test
  def test04_DeclarationAssignationToStringShouldSucceed() = {
    val input = "let a: number = 1;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("a"),VariableTypeNode(NumberVariableType()),ConstantNumb(1.0)))

    assertEquals(expected, parser.parseTokens(tokens))

  }
//
  @Test
  def test05_DeclarationAssignationWithoutLetShouldFail() = {
    val input = " variable: number = 1;"
    val tokens = lexer.tokenize(input)


    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s""))

  }
  @Test
  def test06_DeclarationAssignationShouldFailWithoutTypeAssignationColon() = {
    val input = "let variable number = 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected : but found "))
  }
//
  @Test
  def test07_DeclarationAssignationShouldFailWithoutTypeAssignation() = {
    val input = "let variable: = 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected variable type but found "))

  }
//
  @Test
  def test08_DeclarationAssignationShouldFailWithoutValue() = {
    val input = "let variable: number = ;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s""))

  }

  @Test
  def test09_DeclarationAssignationShouldFailWithoutFinalSemicolon() = {
    val input = "let variable: number = 1"

    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s"Line should end with semicolon"))

  }
//
  @Test
  def test10_singleSemicolonShoudFail() = {
    val input = ";"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains("Expected literal, variable or 'let' but found "))

  }
  @Test
  def test11_DeclarationAssignationShouldFailWithoutEquals() = {
    val input = "let variable: number 1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected = but found "))
  }
  @Test
  def test12_SimpleAdditionDeclarationShouldSucceed() = {
    val input = "let variable: number = 1+1;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),PlusBinaryOperator(),ConstantNumb(1.0))))

    assertEquals(expected, parser.parseTokens(tokens))
  }
//
  @Test
  def test13_SimpleSubstractionDeclarationShouldSucceed() = {
    val input = "let variable: number = 1-1;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),MinusBinaryOperator(),ConstantNumb(1.0))))


    assertEquals(expected, parser.parseTokens(tokens))
  }
//
  @Test
  def test14_AssigningAPlusOperatorShouldFail() = {
    val input = "let variable: number = +;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected expression"))
  }

  @Test
  def test15_AssigningTwoConsecutivePlusOperatorsShouldFail() = {
    val input = "let variable: number = 2++;"

    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(s"Expected expression"))
  }

  @Test
  def test16_singleSemicolonShouldntBeAccepted() = {
    val input = ";"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test17_doubleSemicolonAfterValidExpressionShouldntBeAccepted() = {
    val input = "let variable: number = 1+1;;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test18_SimpleMultiplicationShouldSucceed() = {
    val input = "let variable: number = 1*1;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(1.0))))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test18_SimpleDivisionShouldSucceed() = {
    val input = "let variable: number = 1/1;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(1.0),DivideBinaryOperator(),ConstantNumb(1.0))))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test19_NumberAddedToMultiplicationShouldSucceed() = {
    val input = "let variable: number = 2+1*2;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(2.0)))))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test20_NumberAddedToDivisionShouldSucceed() = {
    val input = "let variable: number = 2+1/2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),BinaryOperation(ConstantNumb(1.0),DivideBinaryOperator(),ConstantNumb(2.0)))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test21_MultiplicationAddedToNumberShouldSucceed() = {
    val input = "let variable: number = 1*2+2;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),BinaryOperation(BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(2.0)),PlusBinaryOperator(),ConstantNumb(2.0))))

    assertEquals(expected, parser.parseTokens(tokens))
  }
  @Test
  def test22_DivisionAddedToNumberShouldSucceed() = {
    val input = "let variable: number = 1/2+2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(1.0),DivideBinaryOperator(),ConstantNumb(2.0)),PlusBinaryOperator(),ConstantNumb(2.0))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))


    val result = parser.parseTokens(tokens)

    assertEquals(expected, result)
}

//  @Test
//  def test23_AdditioninPArenthesisAndThenMultipliedShouldSucceed() = {
//    val input = "let variable: number = (2+1)*2;"
//    val tokens = lexer.tokenize(input)
//
//    val operation = BinaryOperation(BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),ConstantNumb(1.0)),MultiplyBinaryOperator(),ConstantNumb(2.0))
//    val expected = DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation)
//
//
//    assertEquals(expected, parser.parseTokens(tokens))
//  }

  @Test
  def test24_ConsecutiveMultiplicationShouldSuceed() = {
    val input = "let variable: number = 3*4*2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(3), MultiplyBinaryOperator(), ConstantNumb(4)), MultiplyBinaryOperator(), ConstantNumb(2))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))


    assertEquals(expected, parser.parseTokens(tokens))

  }
  @Test
  def test24_ConsecutiveSubstractionShouldSuceed() = {
    val input = "let variable: number = 3/4/2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(3), DivideBinaryOperator(), ConstantNumb(4)), DivideBinaryOperator(), ConstantNumb(2))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test25_ConsecutiveMultiplicationAddedToConstantShouldSuceed() = {
    val input = "let variable: number = 3*4*2+4;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(BinaryOperation(ConstantNumb(3), MultiplyBinaryOperator(), ConstantNumb(4)), MultiplyBinaryOperator(), ConstantNumb(2)), PlusBinaryOperator(), ConstantNumb(4))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))


    assertEquals(expected, parser.parseTokens(tokens))
  }

//  @Test
//  def test26_NumberDividedBySumBetweenParenthesisShouldSuceed() = {
//    val input = "let variable: number = 2*(2+1);"
//    val tokens = lexer.tokenize(input)
//
//    val operation = BinaryOperation(ConstantNumb(2.0),MultiplyBinaryOperator(), BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),ConstantNumb(1.0)))
//    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))
//
//
//    assertEquals(expected, parser.parseTokens(tokens))
//  }

  @Test
  def test27_EndingLineWithDoubleParenthesisShouldFail() = {
    val input = "let variable: number = 2*(2+1));"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test27_MakingEmptyParenthesisAnExpressionShouldFail() = {
    val input = "let variable: number = ();"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test28_variableAssignationShouldFailWithoutFinalSemicolon() = {
    val input = "variable = 3"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains("Line should end with semicolon"))

  }

  @Test
  def test29_assigningAVariableToAVariableShouldSuceed() = {
    val input = "variable = anotherVariable;"
    val tokens = lexer.tokenize(input)

    val expected = List(AssignationNode(Variable("variable"), Variable("anotherVariable")))

    assertEquals(expected, parser.parseTokens(tokens))
}
  @Test
  def test30_assigningASumThatIncludesAVariableToAVariableShouldSuceed() = {
    val input = "variable = anotherVariable + 1;"
    val tokens = lexer.tokenize(input)

    val expected = List(AssignationNode(Variable("variable"), BinaryOperation(Variable("anotherVariable"), PlusBinaryOperator(), ConstantNumb(1.0))))

    assertEquals(expected, parser.parseTokens(tokens))

  }

//  @Test
//  def test31_printSingleNumberShouldSuceed() = {
//    val input = "println(1);"
//    val tokens = lexer.tokenize(input)
//
//    val expected = List(PrintNode(ConstantNumb(1)))
//
//    assertEquals(expected, parser.parseTokens(tokens))
//
//  }
//  @Test
//  def test32_printStringShouldSuceed() = {
//    val input = "println(\"1\");"
//    val tokens = lexer.tokenize(input)
//
//    val expected = List(PrintNode(ConstantString("1")))
//
//    assertEquals(expected, parser.parseTokens(tokens))
//
//  }
//  @Test
//  def test33_printSumShouldSuceed() = {
//    val input = "println(1+2);"
//    val tokens = lexer.tokenize(input)
//
//    val expected = List(PrintNode(BinaryOperation(ConstantNumb(1), PlusBinaryOperator(), ConstantNumb(2))))
//    assertEquals(expected, parser.parseTokens(tokens))
//
//  }
//  @Test
//  def test34_printMultiplicationShouldSuceed() = {
//    val input = "println(1*2);"
//    val tokens = lexer.tokenize(input)
//
//    val expected = List(PrintNode(BinaryOperation(ConstantNumb(1), MultiplyBinaryOperator(), ConstantNumb(2))))
//
//    assertEquals(expected, parser.parseTokens(tokens))
//
//  }
//  @Test
//  def test35_printDivisionShouldSuceed() = {
//    val input = "println(1/2);"
//    val tokens = lexer.tokenize(input)
//
//    val expected = List(PrintNode(BinaryOperation(ConstantNumb(1), DivideBinaryOperator(), ConstantNumb(2))))
//
//    assertEquals(expected, parser.parseTokens(tokens))
//  }
//  @Test
//  def test36_printMultiplicationAddedToContantShouldSuceed() = {
//    val input = "println(1*2+2);"
//    val tokens = lexer.tokenize(input)
//
//    val expected = List(PrintNode(BinaryOperation(BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(2.0)),PlusBinaryOperator(),ConstantNumb(2.0))))
//
//    assertEquals(expected, parser.parseTokens(tokens))
//
//  }
//
//  @Test
//  def test37_printMultiplicationAddedToContantShouldFailWithoutFinalSemicolon() = {
//    val input = "println(1*2+2)"
//    val tokens = lexer.tokenize(input)
//
//    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))
//
//    assertTrue(thrown.getMessage.contains("Line should end with semicolon"))
//
//  }
//  @Test
//  def test38_printConstantShouldFailWithoutFinalSemicolon() = {
//    val input = "println(1)"
//    val tokens = lexer.tokenize(input)
//
//    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))
//
//    assertTrue(thrown.getMessage.contains("Line should end with semicolon"))
//
//  }

    @Test
   def test39_parseMultipleLinesShouldSucceed = {
      val input = "let variable: number = 1.5; \n let variable1 : string = \"s\";";

      val tokens = lexer.tokenize(input)

      val expected = List(
        DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),ConstantNumb(1.5)),
        DeclarationAssignationNode(Variable("variable1"),VariableTypeNode(StringVariableType()),ConstantString("s")))

      val result = parser.parseTokens(tokens)

      assertEquals(expected,result)

    }

  @Test
  def test40_DeclarationAssignationShouldSucceed() = {
    val input = "let a: number = \"a\";"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("a"),VariableTypeNode(NumberVariableType()),ConstantString("a")))

    assertEquals(expected, parser.parseTokens(tokens))

  }

  @Test
  def test41_multipleLinesShouldFailIfFirstOneMissingFinalSemicoloShouldSucceed = {
    val input = "let variable: number = 1.5 \n let variable1 : string = \"s\";";

    val tokens = lexer.tokenize(input)

    val expected = List(
      DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()),ConstantNumb(1.5)),
      DeclarationAssignationNode(Variable("variable1"),VariableTypeNode(StringVariableType()),ConstantString("s")))


      val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

      assertTrue(thrown.getMessage.contains(""))

  }

//  @Test
//  def test41_printExpressionWithPArenthesisShouldSuceed() = {
//    val input = "println((1+2)*3);"
//    val tokens = lexer.tokenize(input)
//
//    //    val expected = List(PrintNode(BinaryOperation(BinaryOperation(ConstantNumb(1.0),MultiplyBinaryOperator(),ConstantNumb(2.0)),PlusBinaryOperator(),ConstantNumb(2.0))))
//    //
//    //    assertEquals(expected, parser.parseTokens(tokens))
//
//  }



//  @Test
//  def test42_consecutiveValidExpressionsShouldFail() = {
//    val input = "variable = 3 4;"
//    val tokens = lexer.tokenize(input)
//
//    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))
//
//    assertTrue(thrown.getMessage.contains(""))
//
//  }
  @Test
  def test43_consecutiveValidExpressionsShouldFail() = {
    val input = "variable = 3++;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }

  @Test
  def test43_consecutiveOperatorsExpressionsShouldFail() = {
    val input = "variable = 3 * +;"
    val tokens = lexer.tokenize(input)


    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }







}