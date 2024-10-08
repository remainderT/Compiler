package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class AddExp implements BasciNode {
    //  AddExp → MulExp | AddExp ('+' | '−') MulExp

    private List<MulExp> mulExps;
    private List<Token> _operations;

    public AddExp(List<MulExp> mulExpNodes, List<Token> _operations) {
        this.mulExps = mulExpNodes;
        _operations = _operations;
    }

    @Override
    public void print() {

    }
}

