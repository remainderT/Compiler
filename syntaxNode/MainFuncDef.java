package syntaxNode;

import common.BasciNode;
import frontend.Token;

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

    @Override
    public void print() {

    }
}
