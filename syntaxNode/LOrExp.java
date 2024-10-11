package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class LOrExp implements BasciNode {
    //  LOrExp → LAndExp | LOrExp '||' LAndExp

    private List<LAndExp> lAndExps;
    private List<Token> orTokens;

    public LOrExp(List<LAndExp> lAndExps, List<Token> orTokens) {
        this.lAndExps = lAndExps;
        this.orTokens = orTokens;
    }

    @Override
    public void print() {

    }
}
