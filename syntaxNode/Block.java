package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class Block implements BasciNode {
    // Block → '{' { BlockItem } '}'

    private Token lbrace;
    private List<BlockItem> blockItems;
    private Token rbrace;

    public Block(Token lbrace, List<BlockItem> blockItems, Token rbrace) {
        lbrace = lbrace;
        this.blockItems = blockItems;
        rbrace = rbrace;
    }

    @Override
    public void print() {

    }
}
