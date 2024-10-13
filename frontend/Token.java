package frontend;

import common.TokenType;

public class Token {

    private TokenType type;

    private String content;

    public Token(TokenType type, String content) {
        this.type = type;
        this.content = content;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return this.getType() + " " + this.getContent() + "\n";
    }

    public static Token getNull() {
        return new Token(TokenType.NULL, "");
    }
}
