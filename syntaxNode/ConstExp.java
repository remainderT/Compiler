package syntaxNode;

import common.BasciNode;

public class ConstExp implements BasciNode {
    // ConstExp → AddExp

    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void print() {

    }
}
