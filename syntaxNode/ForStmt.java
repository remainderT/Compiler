package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

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

    public LVal getLVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void print() {
        lVal.print();
        IO.dealSyntax(assign.toString());
        exp.print();
        IO.dealSyntax(nodeMap.get(SyntaxType.ForStmt));
    }
}
