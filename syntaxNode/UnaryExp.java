package syntaxNode;

import common.BasciNode;
import frontend.Token;

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

    public UnaryExp(FuncFParams funcFParams, Token idenfr, Token lparent, Token rparent) {
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

    }
}
