package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class ConstDef implements BasciNode {
    // Ident [ '[' ConstExp ']' ] '=' ConstInitVal

    private Token idenfr;
    private Token lbrack;
    private ConstExp constExp;
    private Token rbrack;
    private Token assign;
    private ConstInitVal constInitVal;

    public ConstDef(Token idenfr, Token lbrack, ConstExp constExp, Token rbrack, Token assign, ConstInitVal constInitVal) {
        this.idenfr = idenfr;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.constInitVal = constInitVal;
    }

    @Override
    public void print() {

    }
}
