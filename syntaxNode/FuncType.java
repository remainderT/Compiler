package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class FuncType implements BasciNode {
    // FuncType → 'void' | 'int' | 'char'

    private Token voidtk;
    private Token inttk;
    private Token chartk;

    public FuncType(Token voidtk, Token inttk, Token chartk) {
        this.voidtk = voidtk;
        this.inttk = inttk;
        this.chartk = chartk;
    }

    @Override
    public void print() {

    }
}
