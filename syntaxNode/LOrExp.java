package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class LOrExp implements BasciNode {
    //  LOrExp → LAndExp | LOrExp '||' LAndExp

    private List<LAndExp> lAndExps;
    private List<Token> orTokens;

    public LOrExp(List<LAndExp> lAndExps, List<Token> orTokens) {
        this.lAndExps = lAndExps;
        this.orTokens = orTokens;
    }

    public List<LAndExp> getLAndExps() {
        return lAndExps;
    }

    @Override
    public void print() {
        lAndExps.get(0).print();
        IO.dealSyntax(nodeMap.get(SyntaxType.LOrExp));
        for (int i=0; orTokens != null && i < orTokens.size(); i++) {
            IO.dealSyntax(orTokens.get(i).toString());
            lAndExps.get(i+1).print();
            IO.dealSyntax(nodeMap.get(SyntaxType.LOrExp));
        }
    }
}
