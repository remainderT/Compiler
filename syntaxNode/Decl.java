package syntaxNode;

import common.BasciNode;

public class Decl implements BasciNode {
    //  Decl → ConstDecl | VarDecl

    private ConstDecl constDecl;
    private VarDecl varDecl;

    public Decl(ConstDecl constDecl, VarDecl varDecl) {
        this.constDecl = constDecl;
        this.varDecl = varDecl;
    }

    @Override
    public void print() {

    }
}
