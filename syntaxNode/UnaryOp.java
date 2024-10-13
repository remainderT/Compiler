package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

public class UnaryOp implements BasciNode {
    // UnaryOp → '+' | '−' | '!'
    private Token unaryOp;

    public UnaryOp(Token unaryOp) {
        this.unaryOp = unaryOp;
    }

    @Override
    public void print() {
        IO.dealParseOut(unaryOp.toString());
        IO.dealParseOut(nodeMap.get(SyntaxType.UnaryOp));
    }
}
