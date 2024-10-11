package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class LAndExp implements BasciNode {
    //  LAndExp → EqExp | LAndExp '&&' EqExp
    private List<EqExp> eqExps;
    private List<Token> addTokens;

    public LAndExp(List<EqExp> eqExps, List<Token> addTokens) {
        this.eqExps = eqExps;
        this.addTokens = addTokens;
    }

    @Override
    public void print() {

    }
}
