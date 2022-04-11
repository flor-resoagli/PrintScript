import junit.framework.TestCase
import org.junit.jupiter.api.Test
import org.junit.Assert.*

import scala.collection.mutable

class InterpretationTesting {

  def lexer = new DefaultLexerBuilder().build()
  def parser = new Parser()
  def interpreter = new DefaultInterpreterBuilder().build()

  @Test
  def test01_NonExistingVariableAssignationShouldFail(): Unit = {
    val input = "variable = 3;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    assertThrows(classOf[Exception], () => {
      interpreter.interpret(trees)
    })
  }

  @Test
  def test02_shouldSucceedWithEmptyInput(): Unit = {
    val input = ""
    val tokens = lexer.tokenize(input)
    val result = interpreter.interpret(parser.parseTokens(tokens))
    assert(result._2.isEmpty)
    assert(result._1.isEmpty)
  }

  @Test
  def test03_DeclarationAssignationShouldSucceed(): Unit = {
    val input = "let a: number = 1;"
    val tokens = lexer.tokenize(input)
    val result = interpreter.interpret(parser.parseTokens(tokens))
    val expected = mutable.Map("a" -> (NumberVariableType(), 1.0))
    assertEquals(expected, result._2)
  }

  @Test
  def test04_DeclarationAssignationShouldFailWithIncorrectType(): Unit = {
    val input = "let a: string = 1;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    assertThrows(classOf[Exception], () => {
      interpreter.interpret(trees)
    })

    val input2 = "let a: number = \"hola\";"
    val tokens2 = lexer.tokenize(input2)
    val trees2 = parser.parseTokens(tokens2)
    assertThrows(classOf[Exception], () => {
      interpreter.interpret(trees2)
    })
  }

  @Test
  def test05_SimpleAdditionDeclarationShouldSucceed() = {
    val input = "let variable: number = 1+1;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 2.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test06_SimpleSubstractionDeclarationShouldSucceed() = {
    val input = "let variable: number = 1-1;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 0.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test07_SimpleMultiplicationShouldSucceed() = {
    val input = "let variable: number = 2*3;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 6.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test08_SimpleDivisionShouldSucceed() = {
    val input = "let variable: number = 5/2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 2.5))

    assertEquals(expected, result._2)
  }

  @Test
  def test09_NumberAddedToMultiplicationShouldSucceed() = {
    val input = "let variable: number = 2+3*2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 8.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test10_NumberAddedToDivisionShouldSucceed() = {
    val input = "let variable: number = 3+1/2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 3.5))

    assertEquals(expected, result._2)
  }

  @Test
  def test11_MultiplicationAddedToNumberShouldSucceed() = {
    val input = "let variable: number = 3*2+2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 8.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test12_DivisionAddedToNumberShouldSucceed() = {
    val input = "let variable: number = 1/2+2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 2.5))

    assertEquals(expected, result._2)
  }

  @Test
  def test13_AdditioninPArenthesisAndThenMultipliedShouldSucceed() = {
    val input = "let variable: number = (2+1)*2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 6.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test14_ConsecutiveMultiplicationShouldSuceed() = {
    val input = "let variable: number = 3*4*2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 24.0))

    assertEquals(expected, result._2)

  }

  @Test
    def test15_ConsecutiveDivisionShouldSuceed() = {
    val input = "let variable: number = 4/4/2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 0.5))

    assertEquals(expected, result._2)
  }

  @Test
  def test16_ConsecutiveMultiplicationAddedToConstantShouldSuceed() = {
    val input = "let variable: number = 3*4*2+2;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 26.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test17_NumberMultipliedBySumBetweenParenthesisShouldSuceed() = {
    val input = "let variable: number = 2*(2+1);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 6.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test18_assigningAVariableToAVariableShouldSuceed() = {
    val input = "let variable1: number = 2; let variable2: number = variable1;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable1" -> (NumberVariableType(), 2.0), "variable2" -> (NumberVariableType(), 2.0))

    assertEquals(expected, result._2)
  }

  @Test
  def test19_assigningASumThatIncludesAVariableToAVariableShouldSuceed() = {
    val input = "let variable1: number = 2; let variable2: number = variable1+1;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable1" -> (NumberVariableType(), 2.0), "variable2" -> (NumberVariableType(), 3.0))

    assertEquals(expected, result._2)

  }

  @Test
  def test20_printSingleNumberShouldSuceed() = {
    val input = "println(1);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("1.0")

    assertEquals(expected, result._1)

  }

  @Test
  def test21_printStringShouldSuceed() = {
    val input = "println(\"1\");"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("1")

    assertEquals(expected, result._1)
  }

  @Test
  def test22_printSumShouldSuceed() = {
    val input = "println(1+2);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("3.0")

    assertEquals(expected, result._1)

  }

  @Test
  def test23_printMultiplicationShouldSuceed() = {
    val input = "println(1*2);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("2.0")

    assertEquals(expected, result._1)

  }

  @Test
  def test24_printDivisionShouldSuceed() = {
    val input = "println(1/2);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("0.5")

    assertEquals(expected, result._1)
  }

  @Test
  def test25_printMultiplicationAddedToContantShouldSuceed() = {
    val input = "println(1/2+2);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("2.5")

    assertEquals(expected, result._1)
  }

  @Test
  def test26_printMultiplicationAddedToContantShouldFailWithoutFinalSemicolon() = {
    val input = "println(1*2+2);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("4.0")

    assertEquals(expected, result._1)

  }

  @Test
  def test27_parseMultipleLinesShouldSucceed = {
    val input = "let variable: number = 1.5; \n let variable1 : string = \"s\";";
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 1.5), "variable1" -> (StringVariableType(), "s"))

    assertEquals(expected, result._2)

  }

  @Test
  def test28_printExpressionWithPArenthesisShouldSuceed() = {
    val input = "println((1+2)*3);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("9.0")

    assertEquals(expected, result._1)
  }

  @Test
  def test29_divisionFollowedByParenthesisShouldSucceed() = {
    val input = "let variable: number = 2 / (3+2);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 0.4))

    assertEquals(expected, result._2)

  }

  @Test
  def test30_assigningSimpleAdditionBetweenParenthesisShouldSucceed() = {
    val input = "let variable: number = (3+2);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 5.0))

    assertEquals(expected, result._2)

  }

  @Test
  def test31_printingAVariableShouldSucceed() = {
    val input = "let variable: number = 1.5; \n println(variable);"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = List[String]("1.5")

    assertEquals(expected, result._1)

  }

  @Test
  def test32_assigningAVariableShouldSucceed() = {
    val input = "let variable: number = 1.5; \n variable = 2.5;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("variable" -> (NumberVariableType(), 2.5))

    assertEquals(expected, result._2)

  }

  @Test
  def test33_sumOfVariablesShouldSucceed() = {
    val input1 = "let a: number = 1; \n let b: number = 2; \n let c: number = a + b; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (NumberVariableType(), 1.0), "b" -> (NumberVariableType(), 2.0), "c" -> (NumberVariableType(), 3.0))

    assertEquals(expected, result._2)

  }

  @Test
  def test34_subOfVariablesShouldSucceed() = {
    val input1 = "let a: number = 1; \n let b: number = 2; \n let c: number = a - b; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (NumberVariableType(), 1.0), "b" -> (NumberVariableType(), 2.0), "c" -> (NumberVariableType(), -1.0))
    val expectedOutput = List[String]("-1.0")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test35_multOfVariablesShouldSucceed() = {
    val input1 = "let a: number = 3; \n let b: number = 2; \n let c: number = a * b; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (NumberVariableType(), 3.0), "b" -> (NumberVariableType(), 2.0), "c" -> (NumberVariableType(), 6.0))
    val expectedOutput = List[String]("6.0")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test36_sumOfVariableAndConstantShouldSucceed() = {
    val input1 = "let a: number = 3; \n let b: number = 2; \n let c: number = a + b + 1; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (NumberVariableType(), 3.0), "b" -> (NumberVariableType(), 2.0), "c" -> (NumberVariableType(), 6.0))
    val expectedOutput = List[String]("6.0")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test37_sumOfStringsShouldSucceed() = {
    val input1 = "let a: string = \"Hello\"; \n let b: string = \"World\"; \n let c: string = a + b; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (StringVariableType(), "Hello"), "b" -> (StringVariableType(), "World"), "c" -> (StringVariableType(), "HelloWorld"))
    val expectedOutput = List[String]("HelloWorld")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test38_sumOfStringsAndNumbersShouldSucceed() = {
    val input1 = "let a: string = \"Hello\"; \n let b: number = 1; \n let c: string = a + b; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (StringVariableType(), "Hello"), "b" -> (NumberVariableType(), 1.0), "c" -> (StringVariableType(), "Hello1.0"))
    val expectedOutput = List[String]("Hello1.0")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test39_sumOdStringsAndNumbersShouldSucceedInEitherOrder() = {
    val input1 = "let a: number = 2.5; \n let b: string = \"Hello\"; \n let c: string = a + b; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (NumberVariableType(), 2.5), "b" -> (StringVariableType(), "Hello"), "c" -> (StringVariableType(), "2.5Hello"))
    val expectedOutput = List[String]("2.5Hello")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test40_sumOfStringsAndConstantsShouldSucceed() = {
    val input1 = "let b: string = \"Hello\"; \n let c: string = 10 + b; \n println(c);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("b" -> (StringVariableType(), "Hello"), "c" -> (StringVariableType(), "10.0Hello"))
    val expectedOutput = List[String]("10.0Hello")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test41_printingOfANumberShouldSucceed() = {
    val input1 = "let a: number = 2.5; \n println(a);"
    val tokens1 = lexer.tokenize(input1)
    val trees1 = parser.parseTokens(tokens1)
    val result = interpreter.interpret(trees1)

    val expected = mutable.Map("a" -> (NumberVariableType(), 2.5))
    val expectedOutput = List[String]("2.5")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }

  @Test
  def test42_assignNewValueWithVariableUsingSameVariableShouldSucceed() = {
    val input = "let a: number = 2.5; a = a + 1;"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map("a" -> (NumberVariableType(), 3.5))

    assertEquals(expected, result._2)
  }

  @Test
  def test43_printingStringShouldSucceed() = {
    val input = "println(\"Hello\");"
    val tokens = lexer.tokenize(input)
    val trees = parser.parseTokens(tokens)
    val result = interpreter.interpret(trees)

    val expected = mutable.Map()
    val expectedOutput = List[String]("Hello")

    assertEquals(expected, result._2)
    assertEquals(expectedOutput, result._1)
  }
}
