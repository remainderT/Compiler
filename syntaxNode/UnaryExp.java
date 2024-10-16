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
    private FuncRParams funcRParams = null;
    private Token rparent = null;
    private UnaryOp unaryOp = null;
    private UnaryExp unaryExp = null;


    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public UnaryExp(Token idenfr, Token lparent, FuncRParams funcRParams, Token rparent) {
        this.funcRParams = funcRParams;
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
            IO.dealSyntax(idenfr.toString());
            IO.dealSyntax(lparent.toString());
            if (funcRParams != null) {
                funcRParams.print();
            }
            IO.dealSyntax(rparent.toString());
        } else if (unaryOp != null) {
            unaryOp.print();
            unaryExp.print();
        }
        IO.dealSyntax(nodeMap.get(SyntaxType.UnaryExp));
    }
}
