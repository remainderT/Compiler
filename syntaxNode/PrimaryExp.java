package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class PrimaryExp implements BasciNode {
    //  PrimaryExp → '(' Exp ')' | LVal | Number | Character

    private Token lparent;
    private Exp exp;
    private Token rparent;
    private LVal lVal;
    private Number number;
    private Character character;

    public PrimaryExp(Token lparent, Exp exp, Token rparent, LVal lVal, Number number, Character character) {
        this.lparent = lparent;
        this.exp = exp;
        this.rparent = rparent;
        this.lVal = lVal;
        this.number = number;
        this.character = character;
    }

    @Override
    public void print() {

    }
}
