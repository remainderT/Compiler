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
        IO.dealParseOut(idenfr.toString());
        IO.dealParseOut(lparent.toString());
        funcFParams.print();
        IO.dealParseOut(rparent.toString());
        block.print();
        IO.dealParseOut(nodeMap.get(SyntaxType.FuncDef));
    }
}
