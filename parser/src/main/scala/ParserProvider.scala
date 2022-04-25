enum IdentifierState {
  case Declaration, Reassignation, InUse
}

trait ParserProvider {
  def getTokenParser(tokenType: TokenType, identifierState: IdentifierState): TokenTypeParser
  def getParsers(tokenTypes: List[TokenType], state: IdentifierState): List[TokenTypeParser]
}

class ParserProvider10 extends ParserProvider {

  //TODO: No hace falta la separacion del identifier creo, se puede mandar como los otros

  def getTokenParser(tokenType: TokenType, identifierState: IdentifierState): TokenTypeParser ={

    val firstInExpression = List(LITERALNUMBER(), LITERALSTRING(), LEFTPARENTHESIS(), IDENTIFIER())
    val firstInLine = List(DECLARATION(), IDENTIFIER(), PRINTLN())

    tokenType match {
      case LITERALNUMBER() => LiteralNumbParser(List(SUM(), SUB(), DIV(), MUL(),  RIGHTPARENTHESIS()), this, SemicolonParser(firstInLine))
      case LITERALSTRING() => LiteralStringParser(List(SUM(), RIGHTPARENTHESIS()), this, SemicolonParser(firstInLine))
      case SUM() => AdditionParser(firstInExpression, this)
      case SUB() => SubstractionParser(List(LITERALNUMBER(), LEFTPARENTHESIS(), IDENTIFIER()), this)
      case MUL() => HighPriorityOperationParser(List(LITERALNUMBER(), LEFTPARENTHESIS(), IDENTIFIER()), this)
      case DIV() => HighPriorityOperationParser(List(LITERALNUMBER(), LEFTPARENTHESIS(), IDENTIFIER()), this)
      case LEFTPARENTHESIS() => LeftParenthesisParser(firstInExpression, this)
      case RIGHTPARENTHESIS() => RightParenthesisParser(List(SUM(), SUB(), DIV(), MUL(), SEMICOLON()), this, SEMICOLON())
      case DECLARATION() => DeclarationParser(List(StringTypeParser(), NumberTypeParser()), firstInExpression, this, List(DECLARATION()))
      case PRINTLN() => PrintParser(firstInExpression, this)
      case IDENTIFIER() => getIdentifierParser(identifierState)
      case SEMICOLON() => SemicolonParser(firstInLine)
//      case EQUAL() => ValueAssignationParser(firstInExpression)

    }
  }
  def getIdentifierParser(state: IdentifierState): TokenTypeParser ={
    val firstInExpression = List(LITERALNUMBER(), LITERALSTRING(), LEFTPARENTHESIS(), IDENTIFIER())

    state match {
      case IdentifierState.Reassignation => VariableReassignationParser(firstInExpression, this, SEMICOLON())
      case IdentifierState.InUse => VariableParser(List(SUM(), SUB(), DIV(), MUL(),  RIGHTPARENTHESIS()), this)
      case IdentifierState.Declaration => VariableParser(List(SUM(), SUB(), DIV(), MUL(),  RIGHTPARENTHESIS()), this)
    }
  }

  def getParsers(tokenTypes: List[TokenType], state: IdentifierState): List[TokenTypeParser] ={
    tokenTypes.map(getTokenParser(_, state))
  }

}
class ParserProvider11 extends ParserProvider {

  //TODO: No hace falta la separacion del identifier creo, se puede mandar como los otros

  def getTokenParser(tokenType: TokenType, identifierState: IdentifierState): TokenTypeParser ={

    val firstInExpression = List(LITERALNUMBER(), LITERALSTRING(), LEFTPARENTHESIS(), IDENTIFIER())
    val firstInLine = List(DECLARATION(), IDENTIFIER(), PRINTLN())

    tokenType match {
      case LITERALNUMBER() => LiteralNumbParser(List(SUM(), SUB(), DIV(), MUL(),  RIGHTPARENTHESIS()), this, SemicolonParser(firstInLine))
      case LITERALSTRING() => LiteralStringParser(List(SUM(), RIGHTPARENTHESIS()), this, SemicolonParser(firstInLine))
      case SUM() => AdditionParser(firstInExpression, this)
      case SUB() => SubstractionParser(List(LITERALNUMBER(), LEFTPARENTHESIS(), IDENTIFIER()), this)
      case MUL() => HighPriorityOperationParser(List(LITERALNUMBER(), LEFTPARENTHESIS(), IDENTIFIER()), this)
      case DIV() => HighPriorityOperationParser(List(LITERALNUMBER(), LEFTPARENTHESIS(), IDENTIFIER()), this)
      case LEFTPARENTHESIS() => LeftParenthesisParser(firstInExpression, this)
      case RIGHTPARENTHESIS() => RightParenthesisParser(List(SUM(), SUB(), DIV(), MUL(), SEMICOLON()), this, SEMICOLON())
      case DECLARATION() => DeclarationParser(List(StringTypeParser(), NumberTypeParser()), firstInExpression, this, List(DECLARATION(), CONSTANT()))
      case PRINTLN() => PrintParser(firstInExpression, this)
      case IDENTIFIER() => getIdentifierParser(identifierState)
      case SEMICOLON() => SemicolonParser(firstInLine)
      case CONSTANT() => DeclarationParser(List(ConstantNumbParser(), ConstantStringParser()), firstInExpression, this, List(DECLARATION(), CONSTANT()))
      case IF() => IfParser(firstInExpression, this, ConditionParser())
      //      case EQUAL() => ValueAssignationParser(firstInExpression)

    }
  }
  def getIdentifierParser(state: IdentifierState): TokenTypeParser ={
    val firstInExpression = List(LITERALNUMBER(), LITERALSTRING(), LEFTPARENTHESIS(), IDENTIFIER())

    state match {
      case IdentifierState.Reassignation => VariableReassignationParser(firstInExpression, this, SEMICOLON())
      case IdentifierState.InUse => VariableParser(List(SUM(), SUB(), DIV(), MUL(),  RIGHTPARENTHESIS()), this)
      case IdentifierState.Declaration => VariableParser(List(SUM(), SUB(), DIV(), MUL(),  RIGHTPARENTHESIS()), this)
    }
  }

  def getParsers(tokenTypes: List[TokenType], state: IdentifierState): List[TokenTypeParser] ={
    tokenTypes.map(getTokenParser(_, state))
  }

}

