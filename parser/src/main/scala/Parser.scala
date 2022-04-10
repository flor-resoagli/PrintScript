trait Parser {

  def parseTokens(tokens: List[Token]): List[AST]

}
