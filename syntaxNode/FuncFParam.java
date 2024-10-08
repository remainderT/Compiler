package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class FuncFParam implements BasciNode {
    // FuncFParam → BType Ident ['[' ']']
    private BType bType;
    private Token inenfr;
    private Token lbrack;
    private Token rbrack;

    public FuncFParam(BType bType, Token inenfr, Token lbrack, Token rbrack) {
        this.bType = bType;
        this.inenfr = inenfr;
        this.lbrack = lbrack;
        this.rbrack = rbrack;
    }

    @Override
    public void print() {

    }
}
