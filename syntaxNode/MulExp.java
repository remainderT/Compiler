package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class MulExp implements BasciNode {
    //  MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private List<UnaryExp> unaryExps;
    private List<Token> operators;

    public MulExp(List<UnaryExp> unaryExps, List<Token> operators) {
        this.unaryExps = unaryExps;
        this.operators = operators;
    }

    @Override
    public void print() {

    }
}
