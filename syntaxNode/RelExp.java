package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class RelExp implements BasciNode {
    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp

    private List<AddExp> addExps;
    private List<Token> tokens;

    public RelExp(List<AddExp> addExps, List<Token> tokens) {
        this.addExps = addExps;
        this.tokens = tokens;
    }

    public List<AddExp> getAddExps() {
        return addExps;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public void print() {
        addExps.get(0).print();
        IO.dealSyntax(nodeMap.get(SyntaxType.RelExp));
        for (int i=0; tokens != null && i < tokens.size(); i++) {
            IO.dealSyntax(tokens.get(i).toString());
            addExps.get(i+1).print();
            IO.dealSyntax(nodeMap.get(SyntaxType.RelExp));
        }
    }
}
