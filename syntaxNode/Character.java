package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class Character implements BasciNode {
    // Character → CharConst

    private Token chrcon;

    public Character(Token chrcon) {
        this.chrcon = chrcon;
    }

    @Override
    public void print() {

    }
}
