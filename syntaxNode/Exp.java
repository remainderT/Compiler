package syntaxNode;

import common.BasciNode;

public class Exp implements BasciNode {
    //  Exp → AddExp

    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void print() {

    }
}
