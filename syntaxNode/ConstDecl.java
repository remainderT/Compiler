package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Parser.nodeMap;

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
        IO.dealParseOut(consttk.toString());
        bType.print();
        constDefs.get(0).print();
        for (int i=0; i < commas.size(); i++) {
            IO.dealParseOut(commas.get(i).toString());
            constDefs.get(i+1).print();
        }
        IO.dealParseOut(semicn.toString());
        IO.dealParseOut(nodeMap.get(SyntaxType.ConstDecl));

    }
}
