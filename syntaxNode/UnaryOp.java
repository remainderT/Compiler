package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class UnaryOp implements BasciNode {
    // UnaryOp → '+' | '−' | '!'
    private Token unaryOp;

    public UnaryOp(Token unaryOp) {
        this.unaryOp = unaryOp;
    }

    @Override
    public void print() {

    }
}
