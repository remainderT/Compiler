package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class Character implements BasciNode {
    // Character → CharConst

    private Token chrcon;

    public Character(Token chrcon) {
        this.chrcon = chrcon;
    }

    public Token getChrcon() {
        return chrcon;
    }

    @Override
    public void print() {
        IO.dealSyntax(chrcon.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.Character));
    }
}
