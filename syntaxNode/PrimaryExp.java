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

    public PrimaryExp(Token lparent, Exp exp, Token rparent) {
        this.lparent = lparent;
        this.exp = exp;
        this.rparent = rparent;
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
    }

    public PrimaryExp(Number number) {
        this.number = number;
    }

    public PrimaryExp(Character character) {
        this.character = character;
    }

    @Override
    public void print() {

    }
}
