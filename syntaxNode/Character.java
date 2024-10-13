package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

public class Character implements BasciNode {
    // Character → CharConst

    private Token chrcon;

    public Character(Token chrcon) {
        this.chrcon = chrcon;
    }

    @Override
    public void print() {
        IO.dealParseOut(chrcon.toString());
        IO.dealParseOut(nodeMap.get(SyntaxType.Character));
    }
}
