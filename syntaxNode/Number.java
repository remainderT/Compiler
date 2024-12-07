package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class Number implements BasciNode {
    //  Number → IntConst

    private Token intcon;

    public Number(Token intcon) {
        this.intcon = intcon;
    }

    public Token getIntcon() {
        return intcon;
    }

    @Override
    public void print() {
        IO.dealSyntax(intcon.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.Number));
    }
}
