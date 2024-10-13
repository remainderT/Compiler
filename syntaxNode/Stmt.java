package syntaxNode;

import common.BasciNode;
import common.StmtTpye;
import common.SyntaxType;
import frontend.Token;
import util.IO;

import java.util.List;

import static frontend.Parser.nodeMap;

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
        this.type = type;   //  [Exp] ';'
        this.exps = exps;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type,LVal lval, Token assign, List<Exp> exps, Token semicn) {
        this.type = type;    //   LVal '=' Exp ';'
        this.assign = assign;
        this.exps = exps;
        this.lval = lval;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type, LVal lval, Token assign,Token getintOrchartktk, Token lparent, Token rparent, Token semicn) {
        this.type = type;     //  LVal '=' 'getint''('')'';'  |  LVal '=' 'getchar''('')'';'
        this.lval = lval;
        this.getintOrchartk = getintOrchartktk;
        this.assign = assign;
        this.lparent = lparent;
        this.rparent = rparent;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type, Block block) {
        this.block = block;   // Block
        this.type = type;
    }

    public Stmt(StmtTpye type, Token breakOrcontinuetk, Token semicn) {
        this.type = type;      // 'break' ';' | 'continue'
        this.breakOrcontinuetk = breakOrcontinuetk;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type, Token returntk, List<Exp> exps, Token semicn) {
        this.type = type;   // 'return' [Exp] ';'
        this.semicn = semicn;
        this.exps = exps;
        this.returntk = returntk;
    }

    public Stmt(StmtTpye type, Token printtk, Token lparent, Token strcon, List<Exp> exps, List<Token> commas, Token rparent, Token semicn) {
        this.type = type;  // 'printf''('StringConst {','Exp}')'';'
        this.printtk = printtk;
        this.lparent = lparent;
        this.strcon = strcon;
        this.rparent = rparent;
        this.exps = exps;
        this.commas = commas;
        this.semicn = semicn;
    }

    public Stmt(StmtTpye type, Token iftk, Token lparent, Cond cond, Token rparent, Stmt stmt, Token elsetk, Stmt stmtElse) {
        this.iftk = iftk;   // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        this.lparent = lparent;
        this.rparent = rparent;
        this.cond = cond;
        this.type = type;
        this.stmt = stmt;
        this.elsetk = elsetk;
        this.stmtElse = stmtElse;
    }


    public Stmt(StmtTpye type,Token fortk, Token lparent,  ForStmt forStmt1, Token forSemicn1, Cond cond, Token forSemicn2, ForStmt forStmt2, Token rparent, Stmt stmt) {
        this.type = type;      // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
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
        switch (type) {
            case LValAssignExp:  // LVal '=' Exp ';'
                lval.print();
                IO.dealParseOut(assign.toString());
                exps.get(0).print();
                IO.dealParseOut(semicn.toString());
                break;
            case Exp:   // [Exp] ';'
                if (!exps.isEmpty()) {
                    exps.get(0).print();
                }
                IO.dealParseOut(semicn.toString());
                break;
            case Block:  // Block
                block.print();
                break;
            case If:   // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
                IO.dealParseOut(iftk.toString());
                IO.dealParseOut(lparent.toString());
                cond.print();
                IO.dealParseOut(rparent.toString());
                stmt.print();
                if (elsetk != null) {
                    IO.dealParseOut(elsetk.toString());
                    stmtElse.print();
                }
                break;
            case For:  // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
                IO.dealParseOut(fortk.toString());
                IO.dealParseOut(lparent.toString());
                if (forStmt1 != null) {
                    forStmt1.print();
                }
                IO.dealParseOut(forSemicn1.toString());
                if (cond != null) {
                    cond.print();
                }
                IO.dealParseOut(forSemicn2.toString());
                if (forStmt2 != null) {
                    forStmt2.print();
                }
                IO.dealParseOut(rparent.toString());
                stmt.print();
                break;
            case Break:  // 'break' ';'
                IO.dealParseOut(breakOrcontinuetk.toString());
                IO.dealParseOut(semicn.toString());
            case Continue:  // 'continue' ';'
                IO.dealParseOut(breakOrcontinuetk.toString());
                IO.dealParseOut(semicn.toString());
                break;
            case Return:  // 'return' [Exp] ';'
                IO.dealParseOut(returntk.toString());
                if (!exps.isEmpty()) {
                    exps.get(0).print();
                }
                IO.dealParseOut(semicn.toString());
                break;
            case LValAssignGetint:  // LVal '=' 'getint' '(' ')' ';'
                lval.print();
                IO.dealParseOut(assign.toString());
                IO.dealParseOut(getintOrchartk.toString());
                IO.dealParseOut(lparent.toString());
                IO.dealParseOut(rparent.toString());
                IO.dealParseOut(semicn.toString());
                break;
            case LValAssignGetchar:   // LVal '=' 'getchar' '(' ')' ';'
                lval.print();
                IO.dealParseOut(assign.toString());
                IO.dealParseOut(getintOrchartk.toString());
                IO.dealParseOut(lparent.toString());
                IO.dealParseOut(rparent.toString());
                IO.dealParseOut(semicn.toString());
                break;
            case Printf:  // 'printf''('StringConst {','Exp}')'';'
                IO.dealParseOut(printtk.toString());
                IO.dealParseOut(lparent.toString());
                IO.dealParseOut(strcon.toString());
                for (int i = 0; i < commas.size(); i++) {
                    IO.dealParseOut(commas.get(i).toString());
                    exps.get(i).print();
                }
                IO.dealParseOut(rparent.toString());
                IO.dealParseOut(semicn.toString());
                break;
        }
        IO.dealParseOut(nodeMap.get(SyntaxType.Stmt));
    }
}
