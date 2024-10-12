package syntaxNode;

import common.BasciNode;
import common.StmtTpye;
import frontend.Token;

import java.util.List;

public class Stmt implements BasciNode {
/*    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
            | [Exp] ';' //有无Exp两种情况
            | Block
            | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
            | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省，1种情况 2.
            ForStmt与Cond中缺省一个，3种情况 3. ForStmt与Cond中缺省两个，3种情况 4. ForStmt与Cond全部
            缺省，1种情况
            | 'break' ';' | 'continue' ';'
            | 'return' [Exp] ';' // 1.有Exp 2.无Exp
            | LVal '=' 'getint''('')'';'
            | LVal '=' 'getchar''('')'';'
            | 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp
*/
    private StmtTpye type;
    private List<Exp> exps;
    private List<Token> commas;
    private LVal lval;
    private Block block;
    private ForStmt forStmt;
    private Stmt stmt;
    private Stmt stmtElse;
    private Token forSemicn1 = null;
    private Token forSemicn2 = null;
    private ForStmt forStmt1 = null;
    private ForStmt forStmt2 = null;
    private Cond cond;
    private Token assign;
    private Token iftk;
    private Token fortk;
    private Token breakOrcontinuetk;
    private Token returntk;
    private Token getintOrchartk;
    private Token printtk;
    private Token lparent;
    private Token rparent;
    private Token strcon;
    private Token elsetk;
    private Token semicn;


    public Stmt(StmtTpye type, List<Exp> exps, Token semicn) {
        this.type = type;
        this.exps = exps;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type,LVal lval, Token assign, List<Exp> exps, Token semicn) {
        this.type = type;
        this.assign = assign;
        this.exps = exps;
        this.lval = lval;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type, LVal lval, Token assign,Token getintOrchartktk, Token lparent, Token rparent, Token semicn) {
        this.type = type;
        this.lval = lval;
        this.getintOrchartk = getintOrchartktk;
        this.assign = assign;
        this.lparent = lparent;
        this.rparent = rparent;
        this.semicn = semicn;
    }



    public Stmt(StmtTpye type, Block block) {
        this.block = block;
        this.type = type;
    }

    public Stmt(StmtTpye type, Token breakOrcontinuetk, Token semicn) {
        this.type = type;
        this.breakOrcontinuetk = breakOrcontinuetk;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type, Token semicn, List<Exp> exps, Token returntk) {
        this.type = type;
        this.semicn = semicn;
        this.exps = exps;
        this.returntk = returntk;
    }

    public Stmt(StmtTpye type, Token printtk, Token lparent, List<Exp> exps, List<Token> commas, Token rparent, Token semicn) {
        this.type = type;
        this.printtk = printtk;
        this.lparent = lparent;
        this.rparent = rparent;
        this.exps = exps;
        this.commas = commas;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type, Token iftk, Token lparent, Cond cond, Token rparent, Stmt stmt, Token elsetk, Stmt stmtElse) {
        this.iftk = iftk;
        this.lparent = lparent;
        this.rparent = rparent;
        this.cond = cond;
        this.type = type;
        this.stmt = stmt;
        this.elsetk = elsetk;
        this.stmtElse = stmtElse;
    }

    public Stmt(StmtTpye type,Token fortk, Token lparent,  ForStmt forStmt1, Token forSemicn1, Cond cond, Token forSemicn2, ForStmt forStmt2, Token rparent, Stmt stmt) {
        this.type = type;
        this.fortk = fortk;
        this.lparent = lparent;
        this.forStmt1 = forStmt1;
        this.forSemicn1 = forSemicn1;
        this.cond = cond;
        this.forStmt2 = forStmt2;
        this.forSemicn2 = forSemicn2;
        this.rparent = rparent;
        this.stmt = stmt;
    }

    @Override
    public void print() {

    }
}
