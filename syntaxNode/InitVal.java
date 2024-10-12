package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class InitVal implements BasciNode {
    // Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst

    private List<Exp> expList;
    private List<Token> commas;
    private Token lbrace;
    private Token rbrace;
    private Token stringConst;

    public InitVal(Token stringConst) {
        this.stringConst = stringConst;
    }

    public InitVal(List<Exp> expList) {
        this.expList = expList;
    }

    public InitVal(List<Exp> expList, List<Token> commas, Token lbrace, Token rbrace) {
        this.expList = expList;
        this.commas = commas;
        this.lbrace = lbrace;
        this.rbrace = rbrace;
    }

    @Override
    public void print() {

    }
}
