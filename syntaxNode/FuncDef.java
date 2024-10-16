package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

public class FuncDef implements BasciNode {
    //  FuncDef → FuncType Ident '(' [FuncFParams] ')' Block

    private FuncType funcType;
    private Token idenfr;
    private Token lparent;
    private FuncFParams funcFParams;
    private Token rparent;
    private Block block;

    public FuncDef(FuncType funcType, Token idenfr, Token lparent, FuncFParams funcFParams, Token rparent, Block block) {
        this.funcType = funcType;
        this.idenfr = idenfr;
        this.lparent = lparent;
        this.funcFParams = funcFParams;
        this.rparent = rparent;
        this.block = block;
    }

    @Override
    public void print() {
        funcType.print();
        IO.dealSyntax(idenfr.toString());
        IO.dealSyntax(lparent.toString());
        if (funcFParams != null) {
            funcFParams.print();
        }
        IO.dealSyntax(rparent.toString());
        block.print();
        IO.dealSyntax(nodeMap.get(SyntaxType.FuncDef));
    }
}
