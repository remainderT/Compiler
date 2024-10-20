package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class FuncRParams implements BasciNode {
    private List<Exp> exps;
    private List<Token> commas;

    public FuncRParams(List<Exp> exps, List<Token> commas) {
        this.exps = exps;
        this.commas = commas;
    }

    public List<Exp> getExps() {
        return exps;
    }

    @Override
    public void print() {
        exps.get(0).print();
        for (int i = 0; i < commas.size(); i++) {
            IO.dealSyntax(commas.get(i).toString());
            exps.get(i+1).print();
        }
        IO.dealSyntax(nodeMap.get(SyntaxType.FuncRParams));
    }
}
