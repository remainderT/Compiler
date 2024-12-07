package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class FuncType implements BasciNode {
    // FuncType → 'void' | 'int' | 'char'

    private Token token;

    public FuncType(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() {
        IO.dealSyntax(token.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.FuncType));
    }




}
