package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class ConstInitVal implements BasciNode {
    //  ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst

    private ConstExp constExp;
    private Token lbrace;
    private Token rbrace;
    private Token strcon;

    public ConstInitVal(ConstExp constExp, Token lbrace, Token rbrace, Token strcon) {
        this.constExp = constExp;
        this.lbrace = lbrace;
        this.rbrace = rbrace;
        this.strcon = strcon;
    }

    @Override
    public void print() {

    }
}
