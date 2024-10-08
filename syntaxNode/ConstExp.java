package syntaxNode;

import common.BasciNode;

public class ConstExp implements BasciNode {
    // ConstExp → AddExp

    private ConstExp constExp;

    public ConstExp(ConstExp constExp) {
        this.constExp = constExp;
    }

    @Override
    public void print() {

    }
}
