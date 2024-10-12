package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class ConstInitVal implements BasciNode {
    //  ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst

    private List<ConstExp> constExps;
    private List<Token> commas;
    private Token lbrace;
    private Token rbrace;
    private Token strcon;

    public ConstInitVal(Token strcon) {
        this.strcon = strcon;
    }

    public ConstInitVal(List<ConstExp> constExps) {
        this.constExps = constExps;
    }

    public ConstInitVal(List<ConstExp> constExps, List<Token> commas, Token lbrace, Token rbrace) {
        this.constExps = constExps;
        this.commas = commas;
        this.lbrace = lbrace;
        this.rbrace = rbrace;
    }

    @Override
    public void print() {

    }
}
