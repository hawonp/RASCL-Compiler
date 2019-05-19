/*
    Author: Hawon Park (11083842) hawon.park@stonybrook.edu
    Prog:   tokenInfo is an object used to hold information about a token
 */
package Lexer;

public class tokenInfo {
    private Type tokenType;             //token's type
    private String tokenText;

    //collection used to denote "legal" types
    public enum Type {
        SEMICOLON, LPAREN, RPAREN, COMMA, LBRACE, RBRACE, LBRACKET, RBRACKET, ASSIGN,
        LT, GT, EQ, PLUS, MINUS, MULT, DIV, NOT, AND, OR, IF, ELSE, WHILE, INT, FLOAT,
        ID, ICONST, FCONST, COMMENT, INVALID, DD, PRINT, PROGRAM, READ
    }

    //constructor
    public tokenInfo(String text, Type type) {
        this.tokenText = text;
        this.tokenType = type;
    }

    //getters() and toString()
    public Type getTokenType() {
        return tokenType;
    }

    public String getTokenText() {
        return tokenText;
    }

    public String toString(){
        return tokenText + " " + tokenType.toString();
    }

}//class bracket
