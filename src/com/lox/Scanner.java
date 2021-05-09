package com.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

    //Check if anything is in the map after scanning an Identifier
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    //Offsets
    private int start = 0;
    private int current = 0;
    //Tracks what source line <current> is on
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    //Store source code as a simple string
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            //beginning of the next lexeme
            start = current;
            scanSingleToken();
        }

        //Adding new tokens until it reaches End of File
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    //Recognizing lexemes
    private void scanSingleToken() {
        char c = advance();
        switch (c) {
            //it can read Lox stuff like:
            //(( )){} // grouping stuff
            //!*+-/=<> <= == // operators

            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/': {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            }
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            case 'o': {
                if (match('r')) {
                    addToken(OR);
                }
                break;
            }
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            default: {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
            }
            break;
        }


    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);

    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private void number() {
        //Consumes as many digits for the integer part of the literal
        while (isDigit(peek())) advance();

        //look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            //consumes that char
            advance();

            while (isDigit(peek())) advance();
        }

        //Convert lexeme to a numeric value
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    //Making sure there is a digit after the advance
    //So the '.' won't be consumed after the seocnd char
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    //Customized Character.isDigit()
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    //For consuming string literals
    //Handles running out of input before the string is closed and a error will be reported
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }

        //the closing "
        advance();

        //Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    //Consumes the next char in the source file and grabs returns it.
    private char advance() {
        return source.charAt(current++);
    }


    //Outputs of <advance>
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
