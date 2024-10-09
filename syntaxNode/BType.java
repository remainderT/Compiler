package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class BType implements BasciNode {
    // BType → 'int' | 'char'

    private Token token;

    public BType(Token token) {
        this.token = token;
    }

    @Override
    public void print() {

    }
}
