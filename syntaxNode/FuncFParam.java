package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

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
        bType.print();
        IO.dealSyntax(inenfr.toString());
        if (lbrack != null) {
            IO.dealSyntax(lbrack.toString());
            IO.dealSyntax(rbrack.toString());
        }
        IO.dealSyntax(nodeMap.get(SyntaxType.FuncFParam));
    }
}
