package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class Number implements BasciNode {
    //  Number → IntConst

    private Token intcon;

    public Number(Token intcon) {
        this.intcon = intcon;
    }

    @Override
    public void print() {

    }
}
