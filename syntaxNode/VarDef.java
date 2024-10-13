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
        IO.dealParseOut(idenfr.toString());
        if (lbrack != null) {
            IO.dealParseOut(lbrack.toString());
            constExp.print();
            IO.dealParseOut(rbrack.toString());
        }
        if (assign != null) {
            IO.dealParseOut(assign.toString());
            initVal.print();
        }

        IO.dealParseOut(nodeMap.get(SyntaxType.VarDef));
    }
}
