package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import static frontend.Syntax.nodeMap;

public class UnaryExp implements BasciNode {
    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp

    private PrimaryExp primaryExp = null;
    private Token ident = null;
    private Token lparent = null;
    private FuncRParams funcRParams = null;
    private Token rparent = null;
    private UnaryOp unaryOp = null;
    private UnaryExp unaryExp = null;


    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public UnaryExp(Token ident, Token lparent, FuncRParams funcRParams, Token rparent) {
        this.funcRParams = funcRParams;
        this.ident = ident;
        this.lparent = lparent;
        this.rparent = rparent;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    public Token getIdent() {
        return ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    @Override
    public void print() {
        if (primaryExp != null) {
            primaryExp.print();
        } else if (ident != null) {
            IO.dealSyntax(ident.toString());
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
