
import org.junit.Test
import org.junit.Assert._

class LexerTesting {

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
    assertTrue(stringCondition.apply(15, string).get == (23, LITERALSTRING.apply()))
    val string2 = "let variable = \"string with : punctuation * \""
    assertTrue(stringCondition.apply(15, string2).get == (45, LITERALSTRING.apply()))
  }

  @Test
  def test07_tokenize() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "let variable = 1"
    val tokens = lexer.tokenize(input)
    assertTrue(tokens.size == 4)
    assertTrue(tokens(0).tokenType == DECLARATION.apply())
    assertTrue(tokens(1).tokenType == IDENTIFIER.apply())
    assertTrue(tokens(2).tokenType == EQUAL.apply())
    assertTrue(tokens(3).tokenType == LITERALNUMBER.apply())
  }

  @Test
  def test08_token2() = {
    val lexerBuilder = new DefaultLexerBuilder()
    val lexer = lexerBuilder.build()
    val input = "let variable: number = 1.5; \n let otherVariable2:string = \'a string with both numers 44 and punctuation **\';"
    val tokens = lexer.tokenize(input)
    tokens.foreach(println)
    assert(tokens.size == 14)

  }

}