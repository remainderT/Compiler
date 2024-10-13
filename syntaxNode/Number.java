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
        IO.dealParseOut(intcon.toString());
        IO.dealParseOut(nodeMap.get(SyntaxType.Number));
    }
}
