package syntaxNode;

import common.BasciNode;

public class Cond implements BasciNode {
    // Cond → LOrExp

    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    @Override
    public void print() {

    }
}
