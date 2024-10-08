package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class Block implements BasciNode {
    // Block → '{' { BlockItem } '}'

    private Token _lbrace;
    private List<BlockItem> blockItems;
    private Token _rbrace;

    public Block(Token _lbrace, List<BlockItem> blockItems, Token _rbrace) {
        _lbrace = _lbrace;
        this.blockItems = blockItems;
        _rbrace = _rbrace;
    }



    @Override
    public void print() {

    }
}
