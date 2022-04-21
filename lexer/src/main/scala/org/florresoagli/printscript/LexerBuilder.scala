package org.florresoagli.printscript

trait LexerBuilder {
  def build(): Lexer
}

class Lexer10Builder extends LexerBuilder {

  def build(): Lexer = {
    val tokenConditions = List[TokenCondition](
      new ColonCondition(),
      new SemicolonCondition(),
      new EqualCondition(),
      new SumCondition(),
      new SubCondition(),
      new MulCondition(),
      new DivCondition(),
      new LeftParenthesisCondition(),
      new RightParenthesisCondition(),
      new DeclarationCondition(),
      new NumberTypeCondition(),
      new StringTypeCondition(),
      new PrintlnCondition(),
      new IdentifierCondition(),
      new LiteralNumberCondition(),
      new LiteralStringCondition()
    )
    new Lexer(tokenConditions)
  }
}

class Lexer11Builder extends LexerBuilder {

  def build(): Lexer = {
    val tokenConditions = List[TokenCondition](
      new ColonCondition(),
      new SemicolonCondition(),
      new EqualCondition(),
      new SumCondition(),
      new SubCondition(),
      new MulCondition(),
      new DivCondition(),
      new LeftParenthesisCondition(),
      new RightParenthesisCondition(),
      new LeftBraceCondition(),
      new RightBraceCondition(),
      new DeclarationCondition(),
      new ConstantCondition(),
      new NumberTypeCondition(),
      new StringTypeCondition(),
      new BooleanTypeCondition(),
      new PrintlnCondition(),
      new ReadInputCondition(),
      new LiteralBooleanCondition(),
      new IfCondition(),
      new ElseCondition(),
      new IdentifierCondition(),
      new LiteralNumberCondition(),
      new LiteralStringCondition()
    )

    new Lexer(tokenConditions)
  }
}
