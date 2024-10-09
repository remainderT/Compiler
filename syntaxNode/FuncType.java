package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class FuncType implements BasciNode {
    // FuncType → 'void' | 'int' | 'char'

    private Token token;

    public FuncType(Token token) {
        this.token = token;
    }

    @Override
    public void print() {

    }
}
