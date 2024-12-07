package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import common.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class ConstDef implements BasciNode {
    // Ident [ '[' ConstExp ']' ] '=' ConstInitVal

    private Token ident;
    private Token lbrack;
    private ConstExp constExp;
    private Token rbrack;
    private Token assign;
    private ConstInitVal constInitVal;

    public ConstDef(Token ident, Token lbrack, ConstExp constExp, Token rbrack, Token assign, ConstInitVal constInitVal) {
        this.ident = ident;
        this.lbrack = lbrack;
        this.constExp = constExp;
        this.rbrack = rbrack;
        this.assign = assign;
        this.constInitVal = constInitVal;
    }

    public Token getIdent() {
        return ident;
    }

    public int getDimension() {
        return lbrack == null ? 0 : 1;
    }

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    @Override
    public void print() {
        IO.dealSyntax(ident.toString());
        if (lbrack != null) {
            IO.dealSyntax(lbrack.toString());
            constExp.print();
            IO.dealSyntax(rbrack.toString());
        }
        IO.dealSyntax(assign.toString());
        constInitVal.print();
        IO.dealSyntax(nodeMap.get(SyntaxType.ConstDef));
    }
}
