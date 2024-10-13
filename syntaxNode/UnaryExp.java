package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Parser.nodeMap;

public class UnaryExp implements BasciNode {
    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp

    private PrimaryExp primaryExp = null;
    private Token idenfr = null;
    private Token lparent = null;
    private FuncFParams funcFParams = null;
    private Token rparent = null;
    private UnaryOp unaryOp = null;
    private UnaryExp unaryExp = null;


    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public UnaryExp(Token idenfr, Token lparent, FuncFParams funcFParams, Token rparent) {
        this.funcFParams = funcFParams;
        this.idenfr = idenfr;
        this.lparent = lparent;
        this.rparent = rparent;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    @Override
    public void print() {
        if (primaryExp != null) {
            primaryExp.print();
        } else if (idenfr != null) {
            IO.dealParseOut(idenfr.toString());
            IO.dealParseOut(lparent.toString());
            funcFParams.print();
            IO.dealParseOut(rparent.toString());
        } else if (unaryOp != null) {
            IO.dealParseOut(unaryOp.toString());
            unaryExp.print();
        }
        IO.dealParseOut(nodeMap.get(SyntaxType.UnaryExp));
    }
}
