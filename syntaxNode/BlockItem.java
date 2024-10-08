package syntaxNode;

import common.BasciNode;

public class BlockItem implements BasciNode {
    // BlockItem -> Decl | Stmt

    private Decl decl;
    private Stmt stmt;

    public BlockItem(Decl decl, Stmt stmt) {
        this.decl = decl;
        this.stmt = stmt;
    }

    @Override
    public void print() {

    }
}
