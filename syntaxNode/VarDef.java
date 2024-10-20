package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class VarDef implements BasciNode {
    //  VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal

    private Token ident;
    private Token lbrack;
    private ConstExp constExp;
    private Token rbrack;
    private Token assign;
    private InitVal initVal;

    public VarDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack, Token assign, InitVal initVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.initVal = initVal;
    }

    public Token getIdent() {
        return ident;
    }

    public int getDimension() {
        return lbrack == null ? 0 : 1;
    }

    public InitVal getInitVal() {
        return initVal;
    }

    @Override
    public void print() {
        IO.dealSyntax(ident.toString());
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
