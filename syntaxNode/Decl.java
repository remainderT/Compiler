package syntaxNode;

import common.BasciNode;

public class Decl implements BasciNode {
    //  Decl → ConstDecl | VarDecl

    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;

    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }

    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }

    @Override
    public void print() {
        if (constDecl != null) {
            constDecl.print();
        } else {
            varDecl.print();
        }
    }
}
