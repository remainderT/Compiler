package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class AddExp implements BasciNode {
    //  AddExp → MulExp | AddExp ('+' | '−') MulExp

    private List<MulExp> mulExps = null;
    private List<Token> operations = null;

    public AddExp(List<MulExp> mulExpNodes, List<Token> operations) {
        this.mulExps = mulExpNodes;
        this.operations = operations;
    }

    public List<MulExp> getMulExps() {
        return mulExps;
    }

    public List<Token> getOperations() {
        return operations;
    }

    @Override
    public void print() {
        mulExps.get(0).print();
        IO.dealSyntax(nodeMap.get(SyntaxType.AddExp));
        for (int i = 0; operations != null && i < operations.size(); i++) {
            IO.dealSyntax(operations.get(i).toString());
            mulExps.get(i+1).print();
            IO.dealSyntax(nodeMap.get(SyntaxType.AddExp));
        }
    }
}

