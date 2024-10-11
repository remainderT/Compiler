package syntaxNode;

import common.BasciNode;
import frontend.Token;

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

    }
}
