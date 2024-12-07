package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class FuncDef implements BasciNode {
    //  FuncDef → FuncType Ident '(' [FuncFParams] ')' Block

    private FuncType funcType;
    private Token ident;
    private Token lparent;
    private FuncFParams funcFParams;
    private Token rparent;
    private Block block;

    public FuncDef(FuncType funcType, Token ident, Token lparent, FuncFParams funcFParams, Token rparent, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.lparent = lparent;
        this.funcFParams = funcFParams;
        this.rparent = rparent;
        this.block = block;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void print() {
        funcType.print();
        IO.dealSyntax(ident.toString());
        IO.dealSyntax(lparent.toString());
        if (funcFParams != null) {
            funcFParams.print();
        }
        IO.dealSyntax(rparent.toString());
        block.print();
        IO.dealSyntax(nodeMap.get(SyntaxType.FuncDef));
    }
}
