package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Parser.nodeMap;

public class EqExp implements BasciNode {
    //  EqExp → RelExp | EqExp ('==' | '!=') RelExp

    private List<RelExp> relExps;
    private List<Token> tokens;

    public EqExp(List<RelExp> relExps, List<Token> tokens) {
        this.relExps = relExps;
        this.tokens = tokens;
    }

    @Override
    public void print() {
        relExps.get(0).print();
        IO.dealParseOut(nodeMap.get(SyntaxType.EqExp));
        for (int i = 0; i < tokens.size(); i++) {
            IO.dealParseOut(tokens.get(i).toString());
            relExps.get(i+1).print();
        }
    }
}
