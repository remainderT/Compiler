package syntaxNode;

import common.BasciNode;

public class BlockItem implements BasciNode {
    // BlockItem -> Decl | Stmt

    private Decl decl = null;
    private Stmt stmt = null;

    public BlockItem(Decl decl, Stmt stmt) {
        this.decl = decl;
        this.stmt = stmt;
    }

    @Override
    public void print() {
        if (decl != null) {
            decl.print();
        } else {
            stmt.print();
        }
    }
}
