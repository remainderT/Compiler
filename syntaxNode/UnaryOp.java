package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class UnaryOp implements BasciNode {
    // UnaryOp → '+' | '−' | '!'
    private Token unaryOp;

    public UnaryOp(Token unaryOp) {
        this.unaryOp = unaryOp;
    }

    public Token getToken() {
        return unaryOp;
    }

    @Override
    public void print() {
        IO.dealSyntax(unaryOp.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.UnaryOp));
    }
}
