package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class Block implements BasciNode {
    // Block → '{' { BlockItem } '}'

    private Token lbrace;
    private List<BlockItem> blockItems;
    private Token rbrace;

    public Block(Token lbrace, List<BlockItem> blockItems, Token rbrace) {
        this.lbrace = lbrace;
        this.blockItems = blockItems;
        this.rbrace = rbrace;
    }

    public List<BlockItem> getBlockItems() {
        return blockItems;
    }

    public Token getRbrace() {
        return rbrace;
    }

    @Override
    public void print() {
        IO.dealSyntax(lbrace.toString());
        for (BlockItem blockItem : blockItems) {
            blockItem.print();
        }
        IO.dealSyntax(rbrace.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.Block));
    }
}
