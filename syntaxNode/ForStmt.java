package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class ForStmt implements BasciNode {
    //  ForStmt → LVal '=' Exp

    private LVal lVal;
    private Token assign;
    private Exp exp;

    public ForStmt(LVal lVal, Token assign, Exp exp) {
        this.lVal = lVal;
        this.assign = assign;
        this.exp = exp;
    }

    @Override
    public void print() {

    }
}
