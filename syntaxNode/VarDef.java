package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

public class VarDef implements BasciNode {
    //  VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal

    private Token idenfr;
    private Token lbrack;
    private ConstExp constExp;
    private Token rbrack;
    private Token assign;
    private InitVal initVal;

    public VarDef(Token idenfr, Token lbrack, ConstExp constExp, Token rbrack, Token assign, InitVal initVal) {
        this.idenfr = idenfr;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.initVal = initVal;
    }

    @Override
    public void print() {
        IO.dealSyntax(idenfr.toString());
        if (lbrack != null) {
            IO.dealSyntax(lbrack.toString());
            constExp.print();
            IO.dealSyntax(rbrack.toString());
        }
        if (assign != null) {
            IO.dealSyntax(assign.toString());
            initVal.print();
        }

        IO.dealSyntax(nodeMap.get(SyntaxType.VarDef));
    }
}
