package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import java.util.List;

import static frontend.Syntax.nodeMap;

public class ConstDecl implements BasciNode {
    // 'const' BType ConstDef { ',' ConstDef } ';'

    private Token consttk;
    private BType bType;
    private List<ConstDef> constDefs;
    private List<Token> commas;
    private Token semicn;

    public ConstDecl(Token consttk, BType bType, List<ConstDef> constDefs, List<Token> commas, Token semicn) {
        this.consttk = consttk;
        this.bType = bType;
        this.constDefs = constDefs;
        this.commas = commas;
        this.semicn = semicn;
    }

    public List<ConstDef> getConstDefs() {
        return constDefs;
    }

    public BType getBType() {
        return bType;
    }

    @Override
    public void print() {
        IO.dealSyntax(consttk.toString());
        bType.print();
        constDefs.get(0).print();
        for (int i=0; i < commas.size(); i++) {
            IO.dealSyntax(commas.get(i).toString());
            constDefs.get(i+1).print();
        }
        IO.dealSyntax(semicn.toString());
        IO.dealSyntax(nodeMap.get(SyntaxType.ConstDecl));

    }
}
