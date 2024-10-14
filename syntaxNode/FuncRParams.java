package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Parser.nodeMap;

public class FuncRParams implements BasciNode {
    private List<Exp> exps;
    private List<Token> commas;

    public FuncRParams(List<Exp> exps, List<Token> commas) {
        this.exps = exps;
        this.commas = commas;
    }

    @Override
    public void print() {
        exps.get(0).print();
        for (int i = 0; i < commas.size(); i++) {
            IO.dealParseOut(commas.get(i).toString());
            exps.get(i+1).print();
        }
        IO.dealParseOut(nodeMap.get(SyntaxType.FuncRParams));
    }
}
