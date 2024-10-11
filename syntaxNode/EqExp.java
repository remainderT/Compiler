package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class EqExp implements BasciNode {
    //  RelExp | EqExp ('==' | '!=') RelExp

    private List<RelExp> relExps;
    private List<Token> tokens;

    public EqExp(List<RelExp> relExps, List<Token> tokens) {
        this.relExps = relExps;
        this.tokens = tokens;
    }

    @Override
    public void print() {

    }
}
