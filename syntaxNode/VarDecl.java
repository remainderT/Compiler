package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

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

    public List<VarDef> getVarDefs() {
        return varDefs;
    }

    public BType getBType() {
        return bType;
    }

    @Override
    public void print() {
        bType.print();
        varDefs.get(0).print();
        for (int i=0; i<commas.size(); i++) {
            IO.dealSyntax(commas.get(i).toString());
            varDefs.get(i+1).print();
        }
        IO.dealSyntax(semicn.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.VarDecl));
    }
}
