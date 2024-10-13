package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

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
        lVal.print();
        IO.dealParseOut(assign.toString());
        exp.print();
        IO.dealParseOut(nodeMap.get(SyntaxType.ForStmt));
    }
}
