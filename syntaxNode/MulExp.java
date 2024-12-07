package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class MulExp implements BasciNode {
    //  MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private List<UnaryExp> unaryExps;
    private List<Token> operators;

    public MulExp(List<UnaryExp> unaryExps, List<Token> operators) {
        this.unaryExps = unaryExps;
        this.operators = operators;
    }

    public List<Token> getOperators() {
        return operators;
    }

    public List<UnaryExp> getUnaryExps() {
        return unaryExps;
    }

    @Override
    public void print() {
        unaryExps.get(0).print();
        IO.dealSyntax(nodeMap.get(SyntaxType.MulExp));
        for (int i = 0; operators != null && i < operators.size(); i++) {
            IO.dealSyntax(operators.get(i).toString());
            unaryExps.get(i+1).print();
            IO.dealSyntax(nodeMap.get(SyntaxType.MulExp));
        }
    }
}
