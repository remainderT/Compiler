package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class LVal implements BasciNode {
    //  LVal → Ident ['[' Exp ']']

    private Token ident;
    private Token lbrack;
    private Exp exp;
    private Token rbrack;

    public LVal(Token ident, Token lbrack, Exp exp, Token rbrack) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.exp = exp;
        this.rbrack = rbrack;
    }

    public Token getIdent() {
        return ident;
    }

    public Exp getExp() {
        return exp;
    }

    public Token getLbrack() {
        return lbrack;
    }

    @Override
    public void print() {
        IO.dealSyntax(ident.toString());
        if (lbrack != null) {
            IO.dealSyntax(lbrack.toString());
            exp.print();
            IO.dealSyntax(rbrack.toString());
        }
        IO.dealSyntax(nodeMap.get(SyntaxType.LVal));
    }
}
