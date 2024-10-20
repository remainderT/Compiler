package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class InitVal implements BasciNode {
    // Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst

    private List<Exp> exps;
    private List<Token> commas;
    private Token lbrace;
    private Token rbrace;
    private Token stringConst;

    public InitVal(Token stringConst) {
        this.stringConst = stringConst;
    }

    public InitVal(List<Exp> exps) {
        this.exps = exps;
    }

    public InitVal(List<Exp> expList, List<Token> commas, Token lbrace, Token rbrace) {
        this.exps = expList;
        this.commas = commas;
        this.lbrace = lbrace;
        this.rbrace = rbrace;
    }

    public List<Exp> getExps() {
        return exps;
    }

    @Override
    public void print() {
        if (stringConst != null) {
            IO.dealSyntax(stringConst.toString());
        } else if (lbrace != null) {
            IO.dealSyntax(lbrace.toString());
            exps.get(0).print();
            for (int i=0; i<commas.size(); i++) {
                IO.dealSyntax(commas.get(i).toString());
                exps.get(i+1).print();
            }
            IO.dealSyntax(rbrace.toString());
        } else {
            exps.get(0).print();
        }
        IO.dealSyntax(nodeMap.get(SyntaxType.InitVal));
    }
}
