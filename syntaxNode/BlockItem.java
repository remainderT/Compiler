package syntaxNode;

import common.BasciNode;

public class BlockItem implements BasciNode {
    // BlockItem -> Decl | Stmt

    private Decl decl = null;
    private Stmt stmt = null;

    public BlockItem(Decl decl) {
        this.decl = decl;
    }

    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
    }

    @Override
    public void print() {

    }
}
