package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class LAndExp implements BasciNode {
    //  LAndExp → EqExp | LAndExp '&&' EqExp
    private List<EqExp> eqExps;
    private List<Token> addTokens;

    public LAndExp(List<EqExp> eqExps, List<Token> addTokens) {
        this.eqExps = eqExps;
        this.addTokens = addTokens;
    }

    public List<EqExp> getEqExps() {
        return eqExps;
    }

    @Override
    public void print() {
        eqExps.get(0).print();
        IO.dealSyntax(nodeMap.get(SyntaxType.LAndExp));
        for (int i=0; addTokens != null && i < addTokens.size(); i++) {
            IO.dealSyntax(addTokens.get(i).toString());
            eqExps.get(i+1).print();
            IO.dealSyntax(nodeMap.get(SyntaxType.LAndExp));
        }
    }
}
