package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class FuncFParams implements BasciNode {
    //  FuncFParams → FuncFParam { ',' FuncFParam }
    private List<FuncFParam> funcFParams;
    private List<Token> commas;

    public FuncFParams(List<FuncFParam> funcFParams, List<Token> commas) {
        this.funcFParams = funcFParams;
        this.commas = commas;
    }

    @Override
    public void print() {

    }
}
