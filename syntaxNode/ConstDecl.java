package syntaxNode;

import common.BasciNode;
import frontend.Token;

import java.util.List;

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

    @Override
    public void print() {

    }
}
