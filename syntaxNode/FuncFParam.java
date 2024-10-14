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
        IO.dealParseOut(inenfr.toString());
        if (lbrack != null) {
            IO.dealParseOut(lbrack.toString());
            IO.dealParseOut(rbrack.toString());
        }
        IO.dealParseOut(nodeMap.get(SyntaxType.FuncFParam));
    }
}
