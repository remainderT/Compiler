package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class AddExp implements BasciNode {
    //  AddExp → MulExp | AddExp ('+' | '−') MulExp

    private List<MulExp> mulExps = null;
    private List<Token> operations = null;

    public AddExp(List<MulExp> mulExpNodes, List<Token> operations) {
        this.mulExps = mulExpNodes;
        operations = operations;
    }

    @Override
    public void print() {

    }
}

