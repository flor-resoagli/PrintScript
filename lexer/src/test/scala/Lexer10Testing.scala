import junit.framework.TestCase
import org.florresoagli.printscript.{
  COLON,
  ColonCondition,
  DECLARATION,
  DeclarationCondition,
  EQUAL,
  IDENTIFIER,
  IdentifierCondition,
  LITERALNUMBER,
  LITERALSTRING,
  Lexer10Builder,
  LiteralNumberCondition,
  LiteralStringCondition,
  NUMBERTYPE,
  SEMICOLON,
  STRINGTYPE
}
import org.junit.jupiter.api.Test
import org.junit.Assert.*

class Lexer10Testing extends TestCase {
  val lexerBuilder = new Lexer10Builder()
  val lexer = lexerBuilder.build()

  @Test
  def test01_endOfWordMethod() = {
    val tokenCondition = ColonCondition()
    val input = "word"
    val eow = tokenCondition.endOfWord(0, input)
    assert(eow == 3)
    val input2 = "let variable = 1"
    val eowLet = tokenCondition.endOfWord(0, input2)
    assert(eowLet == 2)
    val eowVariable = tokenCondition.endOfWord(5, input2)
    assert(eowVariable == 11)
  }

  @Test
  def test02_singleCharacterCondition() = {
    val colon = ":number"
    val colonCondition = ColonCondition()
    assertTrue(colonCondition.apply(0, colon).get == (0, COLON.apply()))
  }

  @Test
  def test03_declarationCondition() = {
    val declaration = "let variable = 1"
    val declarationCondition = DeclarationCondition()
    assertTrue(declarationCondition.apply(0, declaration).get == (2, DECLARATION.apply()))
  }

  @Test
  def test04_identifierCondition() = {
    val identifier = "let variable = 1"
    val identifierCondition = IdentifierCondition()
    assertTrue(identifierCondition.apply(4, identifier).get == (11, IDENTIFIER.apply()))
  }

  @Test
  def test05_numberCondition() = {
    val number = "let variable = 1"
    val numberCondition = LiteralNumberCondition()
    assertTrue(numberCondition.apply(15, number).get == (15, LITERALNUMBER.apply()))
    val number2 = "let variable = 1.0"
    assertTrue(numberCondition.apply(15, number2).get == (17, LITERALNUMBER.apply()))
  }

  @Test
  def test06_stringCondition() = {
    val string = "let variable = \"string\""
    val stringCondition = LiteralStringCondition()
    assertTrue(stringCondition.apply(15, string).get == (22, LITERALSTRING.apply()))
    val string2 = "let variable = \"string with : punctuation * \""
    assertTrue(stringCondition.apply(15, string2).get == (44, LITERALSTRING.apply()))
  }

  @Test
  def test07_tokenize() = {
    val input = "let variable = 1"
    val tokens = lexer.tokenize(input)
    assertTrue(tokens.size == 4)
    assertTrue(tokens(0).tokenType == DECLARATION.apply())
    assertTrue(tokens(1).tokenType == IDENTIFIER.apply())
    assertTrue(tokens(2).tokenType == EQUAL.apply())
    assertTrue(tokens(3).tokenType == LITERALNUMBER.apply())
  }

  @Test
  def test08_tokenize2() = {
    val input =
      "let variable: number = 1.5; \n let otherVariable2:string = \'a string with both numers 44 and punctuation **\';"
    val tokens = lexer.tokenize(input)
    assert(tokens.size == 14)
    assert(tokens(0).tokenType == DECLARATION.apply())
    assert(tokens(1).tokenType == IDENTIFIER.apply())
    assert(tokens(1).value == "variable")
    assert(tokens(2).tokenType == COLON.apply())
    assert(tokens(3).tokenType == NUMBERTYPE.apply())
    assert(tokens(4).tokenType == EQUAL.apply())
    assert(tokens(5).tokenType == LITERALNUMBER.apply())
    assert(tokens(5).value == "1.5")
    assert(tokens(6).tokenType == SEMICOLON.apply())
    assert(tokens(7).tokenType == DECLARATION.apply())
    assert(tokens(8).tokenType == IDENTIFIER.apply())
    assert(tokens(8).value == "otherVariable2")
    assert(tokens(9).tokenType == COLON.apply())
    assert(tokens(10).tokenType == STRINGTYPE.apply())
    assert(tokens(11).tokenType == EQUAL.apply())
    assert(tokens(12).tokenType == LITERALSTRING.apply())
    assert(tokens(12).value == "\'a string with both numers 44 and punctuation **\'")
    assert(tokens(13).tokenType == SEMICOLON.apply())

  }

  @Test
  def test09_tokenize3() = {
    val input = "let variable: number = 1.5; \n let variable1: number = 1;"
    val tokens = lexer.tokenize(input)
    assert(tokens.size == 14)
    assert(tokens(0).tokenType == DECLARATION.apply())
    assert(tokens(1).tokenType == IDENTIFIER.apply())
    assert(tokens(1).value == "variable")
    assert(tokens(2).tokenType == COLON.apply())
    assert(tokens(3).tokenType == NUMBERTYPE.apply())
    assert(tokens(4).tokenType == EQUAL.apply())
    assert(tokens(5).tokenType == LITERALNUMBER.apply())
    assert(tokens(5).value == "1.5")
    assert(tokens(6).tokenType == SEMICOLON.apply())
    assert(tokens(7).tokenType == DECLARATION.apply())
    assert(tokens(8).tokenType == IDENTIFIER.apply())
    assert(tokens(8).value == "variable1")
    assert(tokens(9).tokenType == COLON.apply())
    assert(tokens(10).tokenType == NUMBERTYPE.apply())
    assert(tokens(11).tokenType == EQUAL.apply())
    assert(tokens(12).tokenType == LITERALNUMBER.apply())
    assert(tokens(12).value == "1")
    assert(tokens(13).tokenType == SEMICOLON.apply())
  }

  @Test
  def test10_tokenizeWithSpacesAtTheEndShouldSucceed() = {
    val input = "let variable = 1;  "
    val tokens = lexer.tokenize(input)
    assertTrue(tokens.size == 5)
    assertTrue(tokens(0).tokenType == DECLARATION.apply())
    assertTrue(tokens(1).tokenType == IDENTIFIER.apply())
    assertTrue(tokens(2).tokenType == EQUAL.apply())
    assertTrue(tokens(3).tokenType == LITERALNUMBER.apply())
    assertTrue(tokens(4).tokenType == SEMICOLON.apply())
  }

  @Test
  def test11_tokenizeWithNewLinesAtTheEndShouldSucceed() = {
    val input = "let variable = 1; \n"
    val tokens = lexer.tokenize(input)
    assertTrue(tokens.size == 5)
    assertTrue(tokens(0).tokenType == DECLARATION.apply())
    assertTrue(tokens(1).tokenType == IDENTIFIER.apply())
    assertTrue(tokens(2).tokenType == EQUAL.apply())
    assertTrue(tokens(3).tokenType == LITERALNUMBER.apply())
    assertTrue(tokens(4).tokenType == SEMICOLON.apply())
  }

}
