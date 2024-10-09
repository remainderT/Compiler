package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

public class VarDecl implements BasciNode {
    //  VarDecl → BType VarDef { ',' VarDef } ';'

    private BType bType;
    private List<VarDef> varDefs;
    private List<Token> commas;
    private Token semicn;

    public VarDecl(BType bType, List<VarDef> varDefs, List<Token> commas, Token semicn) {
        this.bType = bType;
        this.varDefs = varDefs;
        this.commas = commas;
        this.semicn = semicn;
    }

    @Override
    public void print() {

    }
}
