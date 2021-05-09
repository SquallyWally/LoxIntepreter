package com.lox;

public class Token {
    //When it reaches the end of a lexeme, it emits a token
    //it keeps looping from the next char in the source code, up until the end of the input

    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;


    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString(){
        return type+ " " + lexeme + " " + literal;
    }
}
