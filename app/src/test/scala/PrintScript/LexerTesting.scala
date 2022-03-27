package PrintScript

import org.junit.{Before, Test}


class LexerTesting {

  def lexer(input: String): List[Token] =
    val lexer = new Lexer()
    lexer.receiveInput(input)
    lexer.tokenize()
    lexer.returnTokens().toList

  @Test
  def test01_empty_input() =
    val input = ""
    val result = lexer(input)
    assert(result.size == 0)


  @Test
  def test02_single_variant_token() =
    val input = "let: number = 1"
    val result = lexer(input)
    assert(result.size == 5)
    assert(result(0).tokenType.toString == "DECLARATION")
    assert(result(1).tokenType.toString == "COLON")
    assert(result(2).tokenType.toString == "NUMBERTYPE")
    assert(result(3).tokenType.toString == "EQUAL")

  @Test
  def test03_literalnumber_token() =
    val int = "let: number = 1"
    val result = lexer(int)
    assert(result(4).tokenType.toString == "LITERALNUMBER")
    assert(result(4).value == "1")
    val float = "let: number = 1.0"
    val result2 = lexer(float)
    assert(result2(4).tokenType.toString == "LITERALNUMBER")
    assert(result2(4).value == "1.0")
    val negative = "let: number = -1"
    val result3 = lexer(negative)
    println(result3)
//    assert(result3(4).tokenType.toString == "LITERALNUMBER")
//    assert(result3(4).value == "-1")

//NEGATIVE NUMBERS PENDING

  @Test
  def test04_literalstring_token() =
    val string = "let: string = \"Hello World\""
    val result = lexer(string)
    assert(result(4).tokenType.toString == "LITERALSTRING")
    assert(result(4).value == "\"Hello World\"")
    val string2 = "let: string = \"Hello World\"; \n let otherString = \'nother 2 :)\'"
    val result2 = lexer(string2)
    assert(result2(4).tokenType.toString == "LITERALSTRING")
    assert(result2(4).value == "\"Hello World\"")
    assert(result2(9).tokenType.toString == "LITERALSTRING")
    assert(result2(9).value == "\'nother 2 :)\'")





}
