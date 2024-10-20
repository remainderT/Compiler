package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class MainFuncDef implements BasciNode {
    //  MainFuncDef → 'int' 'main' '(' ')' Block

    private Token inttk;
    private Token maintk;
    private Token lparent;
    private Token rparent;
    private Block block;

    public MainFuncDef(Token inttk, Token maintk, Token lparent, Token rparent, Block block) {
        this.inttk = inttk;
        this.maintk = maintk;
        this.lparent = lparent;
        this.rparent = rparent;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void print() {
        IO.dealSyntax(inttk.toString());
        IO.dealSyntax(maintk.toString());
        IO.dealSyntax(lparent.toString());
        IO.dealSyntax(rparent.toString());
        block.print();
        IO.dealSyntax(nodeMap.get(SyntaxType.MainFuncDef));
    }
}
