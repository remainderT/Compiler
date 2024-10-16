package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

public class LVal implements BasciNode {
    //  LVal → Ident ['[' Exp ']']

    private Token idenfr;
    private Token lbrack;
    private Exp exp;
    private Token rbrack;

    public LVal(Token idenfr, Token lbrack, Exp exp, Token rbrack) {
        this.idenfr = idenfr;
        this.lbrack = lbrack;
        this.exp = exp;
        this.rbrack = rbrack;
    }

    @Override
    public void print() {
        IO.dealSyntax(idenfr.toString());
        if (lbrack != null) {
            IO.dealSyntax(lbrack.toString());
            exp.print();
            IO.dealSyntax(rbrack.toString());
        }
        IO.dealSyntax(nodeMap.get(SyntaxType.LVal));
    }
}
