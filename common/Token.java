package common;

public class Token {

    private TokenType type;

    private String content;

    private int lineNumber;

    public Token(TokenType type, String content, int lineNumber) {
        this.type = type;
        this.content = content;
        this.lineNumber = lineNumber;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public int getLineNumber() { return this.lineNumber; }

    @Override
    public String toString() {
        return this.getType() + " " + this.getContent() + "\n";
    }

    public static Token getNull() {
        return new Token(TokenType.NULL, "", 0);
    }
}
