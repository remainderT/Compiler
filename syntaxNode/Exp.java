package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import util.IO;

import static frontend.Parser.nodeMap;

public class Exp implements BasciNode {
    //  Exp → AddExp

    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void print() {
        addExp.print();
        IO.dealSyntax(nodeMap.get(SyntaxType.Exp));
    }
}
