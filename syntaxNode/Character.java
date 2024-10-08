package syntaxNode;

import common.BasciNode;
import frontend.Token;

public class Character implements BasciNode {
    // Character → CharConst

    private Token chrcont;

    public Character(Token chrcont) {
        this.chrcont = chrcont;
    }

    @Override
    public void print() {

    }
}
