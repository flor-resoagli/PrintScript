import junit.framework.TestCase
import org.junit.jupiter.api.{Test}
import org.junit.Assert.*

class ParserTesting {

  def lexer: Lexer = new DefaultLexerBuilder().build()
  def parser: DefaultParser = new DefaultParser()



  @Test
  def test01_singleLiteralNumberShouldFail(): Unit = {
    val input = "1;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }
//
  @Test
  def test02_variableAssignationShouldSuceed(): Unit = {
    val input = "variable = 3;"
    val tokens = lexer.tokenize(input)

    val expected = List(AssignationNode(Variable("variable"), ConstantNumb(3)))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test03_shouldSucceedWithEmptyInput(): Unit = {
    val input = ""
    val tokens = lexer.tokenize(input)


    val expected = List().empty
    assertEquals(expected, parser.parseTokens(tokens))
  }

//

  @Test
  def test04_DeclarationAssignationToStringShouldSucceed(): Unit = {
    val input = "let a: number = 1;"
    val tokens = lexer.tokenize(input)

    val expected = List(DeclarationAssignationNode(Variable("a"),VariableTypeNode(NumberVariableType()),ConstantNumb(1.0)))

    assertEquals(expected, parser.parseTokens(tokens))

  }
//
  @Test
  def test05_DeclarationAssignationWithoutLetShouldFail(): Unit = {
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
  def test52_SimpleDivisionShouldSucceed() = {
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

  @Test
  def test23_AdditioninPArenthesisAndThenMultipliedShouldSucceed() = {
    val input = "let variable: number = (2+1)*2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),ConstantNumb(1.0)),MultiplyBinaryOperator(),ConstantNumb(2.0))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))


    assertEquals(expected, parser.parseTokens(tokens))
  }

  @Test
  def test24_ConsecutiveMultiplicationShouldSuceed() = {
    val input = "let variable: number = 3*4*2;"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(3), MultiplyBinaryOperator(), ConstantNumb(4)), MultiplyBinaryOperator(), ConstantNumb(2))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))


    assertEquals(expected, parser.parseTokens(tokens))

  }
  @Test
  def test51_ConsecutiveSubstractionShouldSuceed() = {
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

  @Test
  def test26_NumberMultipliedBySumBetweenParenthesisShouldSuceed() = {
    val input = "let variable: number = 2*(2+1);"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(ConstantNumb(2.0),MultiplyBinaryOperator(), BinaryOperation(ConstantNumb(2.0),PlusBinaryOperator(),ConstantNumb(1.0)))
    val expected = List(DeclarationAssignationNode(Variable("variable"),VariableTypeNode(NumberVariableType()), operation))

    val result = parser.parseTokens(tokens)

    assertEquals(expected, result)
  }

  @Test
  def test27_EndingLineWithDoubleParenthesisShouldFail() = {
    val input = "let variable: number = 2*(2+1));"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))
  }

  @Test
  def test50_MakingEmptyParenthesisAnExpressionShouldFail() = {
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

  @Test
  def test31_printSingleNumberShouldSuceed() = {
    val input = "println(1);"
    val tokens = lexer.tokenize(input)

    val expected = List(PrintNode(ConstantNumb(1)))

    assertEquals(expected, parser.parseTokens(tokens))

  }
  @Test
  def test32_printStringShouldSuceed() = {
    val input = "println(\"1\");"
    val tokens = lexer.tokenize(input)

    val expected = List(PrintNode(ConstantString("1")))

    assertEquals(expected, parser.parseTokens(tokens))

  }
  @Test
  def test33_printSumShouldSuceed() = {
    val input = "println(1+2);"
    val tokens = lexer.tokenize(input)

    val expected = List(PrintNode(BinaryOperation(ConstantNumb(1), PlusBinaryOperator(), ConstantNumb(2))))
    val result = parser.parseTokens(tokens)
    assertEquals(expected, result)

  }
  @Test
  def test34_printMultiplicationShouldSuceed() = {
    val input = "println(1*2);"
    val tokens = lexer.tokenize(input)

    val expected = List(PrintNode(BinaryOperation(ConstantNumb(1), MultiplyBinaryOperator(), ConstantNumb(2))))

    assertEquals(expected, parser.parseTokens(tokens))

  }
  @Test
  def test35_printDivisionShouldSuceed() = {
    val input = "println(1/2);"
    val tokens = lexer.tokenize(input)

    val expected = List(PrintNode(BinaryOperation(ConstantNumb(1), DivideBinaryOperator(), ConstantNumb(2))))

    assertEquals(expected, parser.parseTokens(tokens))
  }
  @Test
  def test36_printMultiplicationAddedToContantShouldSuceed() = {
    val input = "println(1/2+2);"
    val tokens = lexer.tokenize(input)

    val operation = BinaryOperation(BinaryOperation(ConstantNumb(1.0),DivideBinaryOperator(),ConstantNumb(2.0)),PlusBinaryOperator(),ConstantNumb(2.0))
    val expected = List(PrintNode(operation))


    val result = parser.parseTokens(tokens)

    assertEquals(expected, result)

  }

  @Test
  def test37_printMultiplicationAddedToContantShouldFailWithoutFinalSemicolon() = {
    val input = "println(1*2+2)"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }
  @Test
  def test53_printaloneShouldFail() = {
    val input = "print"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }
  @Test
  def test54_printAloneWithOpenParenthesusShouldFail() = {
    val input = "print(;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }

  @Test
  def test38_printConstantShouldFailWithoutFinalSemicolon() = {
    val input = "println(1)"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains("Line should end with semicolon"))

  }

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


      val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

      assertTrue(thrown.getMessage.contains(""))

  }

  @Test
  def test49_printExpressionWithPArenthesisShouldSuceed(): Unit = {
    val input = "println((1+2)*3);"
    val tokens = lexer.tokenize(input)

    val expected = List(PrintNode(BinaryOperation(BinaryOperation(ConstantNumb(1.0),PlusBinaryOperator(),ConstantNumb(2.0)),MultiplyBinaryOperator(),ConstantNumb(3.0))))

    val result = parser.parseTokens(tokens)

    assertEquals(expected,result)
  }



  @Test
  def test42_consecutiveValidExpressionsShouldFail(): Unit = {
    val input = "variable = 3 4;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }
  @Test
  def test43_consecutiveValidExpressionsShouldFail(): Unit= {
    val input = "variable = 3++;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }

  @Test
  def test48_consecutiveOperatorsExpressionsShouldFail(): Unit = {
    val input = "variable = 3 * +;"
    val tokens = lexer.tokenize(input)


    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }
  @Test
  def test44_expressionEndingWithLeftParenthesisShouldFail(): Unit = {
    val input = "variable = 3 + (;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }
  @Test
  def test45_expressionStartingWithParenthesisShouldFail(): Unit= {
    val input = "variable = ) + 3;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }

  @Test
  def test46_divisionFollowedByParenthesisShouldSucceed(): Unit = {
    val input = "variable = 2 / (3+2);"
    val tokens = lexer.tokenize(input)


    val expected  = List(AssignationNode(Variable("variable"),BinaryOperation(ConstantNumb(2.0),DivideBinaryOperator(),BinaryOperation(ConstantNumb(3.0),PlusBinaryOperator(),ConstantNumb(2.0)))))

    val result = parser.parseTokens(tokens)

    assertEquals(expected, result)


  }

  @Test
  def test47_assigningSimpleAdditionBetweenParenthesisShouldSucceed(): Unit = {
    val input = "variable = (3+2);"
    val tokens = lexer.tokenize(input)

    val expected  = List(AssignationNode(Variable("variable"),BinaryOperation(ConstantNumb(3.0),PlusBinaryOperator(),ConstantNumb(2.0))))

    val result = parser.parseTokens(tokens)

    assertEquals(expected, result)


  }
  @Test
  def test55_numberFollowedByIdentifierExpressionsShouldFail(): Unit ={
    val input = "variable = 3 otherVariable;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }

  @Test
  def test56_numberFollowedByLeftParenthesisExpressionsShouldFail(): Unit ={
    val input = "variable = 3 (;"
    val tokens = lexer.tokenize(input)

    val thrown = assertThrows(classOf[Exception], () => parser.parseTokens(tokens))

    assertTrue(thrown.getMessage.contains(""))

  }

  @Test
  def test57_concatOfStringsWithPlusOperatorShouldSucceed(): Unit = {
    val input = "variable = \"hola\"+\"hola2\";"
    val tokens = lexer.tokenize(input)

    val expected  = List(AssignationNode(Variable("variable"),BinaryOperation(ConstantString("hola"),PlusBinaryOperator(),ConstantString("hola2"))))

    val result = parser.parseTokens(tokens)

    assertEquals(expected, result)
  }






}