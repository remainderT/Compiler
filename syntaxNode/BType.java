package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class BType implements BasciNode {
    // BType → 'int' | 'char'

    private Token inttk;
    private Token chartk;

    public BType(Token inttk, Token chartk) {
        this.inttk = inttk;
        this.chartk = chartk;
    }

    @Override
    public void print() {

    }
}
