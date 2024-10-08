package frontend;

import common.LexicalException;
import common.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lexer {

    private static HashMap<String, TokenType> tokenMap = new HashMap<>();

    private static HashMap<String, TokenType> compareMap = new HashMap<>();

    private static int commentFlag = 0; // 0： 初始  1：单行   2:多行start  3：多行end
    private static int lineNumber = 1;

    static {
        tokenMap.put("main", TokenType.MAINTK);
        tokenMap.put("const", TokenType.CONSTTK);
        tokenMap.put("int", TokenType.INTTK);
        tokenMap.put("char", TokenType.CHARTK);
        tokenMap.put("break", TokenType.BREAKTK);
        tokenMap.put("continue", TokenType.CONTINUETK);
        tokenMap.put("if", TokenType.IFTK);
        tokenMap.put("else", TokenType.ELSETK);
        tokenMap.put("for", TokenType.FORTK);
        tokenMap.put("getint", TokenType.GETINTTK);
        tokenMap.put("getchar", TokenType.GETCHARTK);
        tokenMap.put("printf", TokenType.PRINTFTK);
        tokenMap.put("return", TokenType.RETURNTK);
        tokenMap.put("void", TokenType.VOIDTK);

        compareMap.put("<", TokenType.LSS);
        compareMap.put("<=", TokenType.LEQ);
        compareMap.put(">", TokenType.GRE);
        compareMap.put(">=", TokenType.GEQ);
        compareMap.put("==", TokenType.EQL);
        compareMap.put("!=", TokenType.NEQ);
        compareMap.put("=", TokenType.ASSIGN);
        compareMap.put("!", TokenType.NOT);
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private static boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private static boolean isWhitespace(char c) {
        if (c == '\n') {
            lineNumber++;
        }
        return Character.isWhitespace(c) || c == '\n';
    }

    public static List<Token> analyze(String content) throws Exception{
        List<Token> tokens = new ArrayList<>();
        int length = content.length();
        int state = 0;
        StringBuilder currentToken = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = content.charAt(i);
            switch (state) {
                case 0: // 初始
                    if (isLetter(c)) { // 可能是关键字或标识符
                        currentToken.append(c);
                        state = 1;
                    } else if (isDigit(c)) {
                        currentToken.append(c);
                        state = 2;
                    } else if (c == '[') {
                        tokens.add(new Token(TokenType.LBRACK, "["));
                    } else if (c == ']') {
                        tokens.add(new Token(TokenType.RBRACK, "]"));
                    } else if (c == '{') {
                        tokens.add(new Token(TokenType.LBRACE, "{"));
                    } else if (c == '}') {
                        tokens.add(new Token(TokenType.RBRACE, "}"));
                    } else if (c == ',') {
                        tokens.add(new Token(TokenType.COMMA, ","));
                    } else if (c == ';') {
                        tokens.add(new Token(TokenType.SEMICN, ";"));
                    } else if (c == '(') {
                        tokens.add(new Token(TokenType.LPARENT, "("));
                    } else if (c == ')') {
                        tokens.add(new Token(TokenType.RPARENT, ")"));
                    } else if (c == '&') {
                        if (content.charAt(i + 1) == '&') {
                            i++;
                            tokens.add(new Token(TokenType.AND, "&&"));
                        } else {
                            throw new LexicalException(lineNumber + " a");
                        }
                    } else if (c == '|') {
                        if (content.charAt(i + 1) == '|') {
                            i++;
                            tokens.add(new Token(TokenType.OR, "||"));
                        } else {
                            throw new LexicalException(lineNumber + " a");
                        }
                    } else if (c == '\'') { // 字符常量
                        char ch = content.charAt(i + 1);
                        String tokenValue;
                        if (ch == '\\') {
                            tokenValue = content.substring(i+1, i+3);
                            i++;
                        } else {
                            tokenValue = content.substring(i+1, i+2);
                        }
                        tokens.add(new Token(TokenType.CHRCON, '\'' + tokenValue + '\''));
                        i = i + 2;
                    } else if (c == '"') { // 字符串常量
                        currentToken.append(c);
                        state = 3;
                    } else if (c == '+') {
                        tokens.add(new Token(TokenType.PLUS, "+"));
                    } else if (c == '-') {
                        tokens.add(new Token(TokenType.MINU, "-"));
                    } else if (c == '*') {
                        tokens.add(new Token(TokenType.MULT, "*"));
                    } else if (c == '%') {
                        tokens.add(new Token(TokenType.MOD, "%"));
                    } else if (isWhitespace(c)) {
                        // 忽略
                    } else if (c == '!' || c == '<' || c == '>' || c == '=') {
                        currentToken.append(c);
                        state = 4;
                    } else if (c == '/') {
                        state = 5;
                    }
                    break;
                case 1: // 关键字或标识符
                    if (isLetter(c) || isDigit(c)) {
                        currentToken.append(c);
                    } else {
                        i--; // 回退一个字符
                        String tokenValue = currentToken.toString();
                        TokenType tokenType = tokenMap.getOrDefault(tokenValue, TokenType.IDENFR);
                        tokens.add(new Token(tokenType, tokenValue));
                        currentToken.setLength(0);
                        state = 0;
                    }
                    break;
                case 2: // 数字
                    if (isDigit(c)) {
                        currentToken.append(c);
                    } else {
                        i--; // 回退一个字符
                        tokens.add(new Token(TokenType.INTCON, currentToken.toString()));
                        currentToken.setLength(0);
                        state = 0;
                    }
                    break;
                case 3: // 字符串
                    currentToken.append(c);
                    if (c == '"') {
                        tokens.add(new Token(TokenType.STRCON, currentToken.toString()));
                        currentToken.setLength(0);
                        state = 0;
                    }
                    break;
                case 4: // 比较运算符
                    if (c == '=') {   // 双目
                        currentToken.append(c);
                    } else {
                        i--;
                    }
                    String tokenValue = currentToken.toString();
                    TokenType tokenType = compareMap.get(tokenValue);
                    tokens.add(new Token(tokenType, tokenValue));
                    state = 0;
                    currentToken.setLength(0);
                    break;
                case 5: //   /号 和 注释相关
                    if (c == '*') {
                        if (commentFlag == 0) {
                            commentFlag = 2;
                        } if (commentFlag == 2 && content.charAt(i + 1) == '/') {
                            i++;
                            commentFlag = 0;
                            state = 0;
                        }
                    } else if (c == '/') {
                        if (commentFlag == 0) {
                            commentFlag = 1;
                        }
                    } else if (c == '\n') {
                        if (commentFlag == 1) {
                            commentFlag = 0;
                            lineNumber++;
                            state = 0;
                        }
                    } else if (commentFlag == 0){   // 不是注释是除法
                        i--;
                        tokens.add(new Token(TokenType.DIV, "/"));
                        state = 0;
                    }
                    break;
            }
        }
        return tokens;
    }

}
