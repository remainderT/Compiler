package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

public class Number implements BasciNode {
    //  Number → IntConst

    private Token intcon;

    public Number(Token intcon) {
        this.intcon = intcon;
    }

    @Override
    public void print() {
        IO.dealSyntax(intcon.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.Number));
    }
}
