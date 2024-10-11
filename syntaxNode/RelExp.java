package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class RelExp implements BasciNode {
    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp

    private List<AddExp> addExps;
    private List<Token> tokens;

    public RelExp(List<AddExp> addExps, List<Token> tokens) {
        this.addExps = addExps;
        this.tokens = tokens;
    }

    @Override
    public void print() {

    }
}
