package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class VarDef implements BasciNode {
    //  VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal

    private Token idenfr;
    private Token lbrack;
    private ConstExp constExp;
    private Token rbrack;
    private Token assign;
    private InitVal initVal;

    public VarDef(Token idenfr, Token lbrack, ConstExp constExp, Token rbrack, Token assign, InitVal initVal) {
        this.idenfr = idenfr;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.initVal = initVal;
    }

    @Override
    public void print() {

    }
}
