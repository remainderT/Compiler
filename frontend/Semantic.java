package frontend;

import common.Error;
import common.ErrorType;
import symbol.Symbol;
import symbol.SymbolTable;
import syntaxNode.AddExp;
import syntaxNode.BType;
import syntaxNode.Block;
import syntaxNode.BlockItem;
import syntaxNode.CompUnit;
import syntaxNode.Cond;
import syntaxNode.ConstDecl;
import syntaxNode.ConstDef;
import syntaxNode.ConstExp;
import syntaxNode.ConstInitVal;
import syntaxNode.Decl;
import syntaxNode.EqExp;
import syntaxNode.Exp;
import syntaxNode.ForStmt;
import syntaxNode.FuncDef;
import syntaxNode.FuncFParam;
import syntaxNode.FuncFParams;
import syntaxNode.FuncRParams;
import syntaxNode.InitVal;
import syntaxNode.LAndExp;
import syntaxNode.LOrExp;
import syntaxNode.LVal;
import syntaxNode.MainFuncDef;
import syntaxNode.MulExp;
import syntaxNode.PrimaryExp;
import syntaxNode.RelExp;
import syntaxNode.Stmt;
import syntaxNode.UnaryExp;
import syntaxNode.VarDecl;
import syntaxNode.VarDef;
import util.SymbolFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Semantic {

    private SymbolTable currentSymbolTable;

    private List<SymbolTable> symbolTables;

    private List<Error> errors;

    int index;  // 符号表编号


    public Semantic(List<Error> errors) {
        index = 1;
        symbolTables = new ArrayList<>();
        this.currentSymbolTable = new SymbolTable(1, null);
        symbolTables.add(currentSymbolTable);
        this.errors = errors;
    }

    public List<SymbolTable> getSymbolTables() {
        return symbolTables;
    }

    public void fCompUnit(CompUnit compUnit) {
        // CompUnit -> {Decl} {FuncDef} MainFuncDef
        for (Decl decl : compUnit.getDecls()) {
            fDecl(decl);
        }
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            fFuncDef(funcDef);
        }
        fMainFunDef(compUnit.getMainFuncDef());
    }

    public void fDecl(Decl decl) {
        // Decl -> ConstDecl | VarDecl
        if (decl.getConstDecl() != null) {
            fConstDecl(decl.getConstDecl());
        } else {
            fVarDecl(decl.getVarDecl());
        }
    }

    public void fFuncDef(FuncDef funcDef) {
        //  FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        Token ident = funcDef.getIdent();
        if (currentSymbolTable.contains(ident.getContent())) {
            errors.add(new Error(ident.getLineNumber(), ErrorType.b));
        }
        currentSymbolTable.put(ident.getContent(), SymbolFactory.buildFunc(funcDef.getFuncType(), ident.getContent()));
        SymbolTable parent = currentSymbolTable;
        currentSymbolTable = new SymbolTable(++index, parent);
        symbolTables.add(currentSymbolTable);
        if (funcDef.getFuncFParams() != null) {
            fFuncFParams(funcDef.getFuncFParams());
        }
        fBlock(funcDef.getBlock());
        currentSymbolTable = parent;
    }

    public void fMainFunDef(MainFuncDef mainFuncDef) {
        // MainFuncDef -> 'void' 'main' '(' ')' Block
        SymbolTable parent = currentSymbolTable;
        currentSymbolTable = new SymbolTable(++index, parent);
        symbolTables.add(currentSymbolTable);
        fBlock(mainFuncDef.getBlock());
        currentSymbolTable = parent;
    }

    public void fConstDecl(ConstDecl constDecl) {
        // ConstDecl -> 'const' BType ConstDef {',' ConstDef} ';'
        for (ConstDef constDef : constDecl.getConstDefs()) {
            fConstDef(constDef, constDecl.getBType());
        }
    }

    public void fVarDecl(VarDecl varDecl) {
        // VarDecl -> BType VarDef {',' VarDef} ';'
        for (VarDef varDef : varDecl.getVarDefs()) {
            fVarDef(varDef, varDecl.getBType());
        }
    }

    public void fConstDef(ConstDef constDef, BType bType) {
        // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
        Token ident = constDef.getIdent();
        if (currentSymbolTable.contains(ident.getContent())) {
            errors.add(new Error(ident.getLineNumber(), ErrorType.b));
        }
        int dimension = constDef.getDimension();
        if (dimension == 0) {
            currentSymbolTable.put(ident.getContent(), SymbolFactory.buildConst0(bType, ident.getContent()));
        } else {
            currentSymbolTable.put(ident.getContent(), SymbolFactory.buildConst1(bType, ident.getContent()));
        }
        if (constDef.getConstInitVal() != null) {
            fConstInitVal(constDef.getConstInitVal());
        }
    }

    public void fVarDef(VarDef varDef, BType bType) {
        // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
        Token ident = varDef.getIdent();
        if (currentSymbolTable.contains(ident.getContent())) {
            errors.add(new Error(ident.getLineNumber(), ErrorType.b));
        }
        int dimension = varDef.getDimension();
        if (dimension == 0) {
            currentSymbolTable.put(ident.getContent(), SymbolFactory.buildVar0(bType, ident.getContent()));
        } else {
            currentSymbolTable.put(ident.getContent(), SymbolFactory.buildVar1(bType, ident.getContent()));
        }
        if (varDef.getInitVal() != null) {
            fInitVal(varDef.getInitVal());
        }
    }

    public void fInitVal(InitVal initVal) {
        // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        if (initVal.getExps() != null) {
            for (Exp exp : initVal.getExps()) {
                fExp(exp);
            }
        }
    }

    public void fConstInitVal(ConstInitVal constInitVal) {
        // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
        if (constInitVal.getConstExps() != null) {
            for (ConstExp constExp : constInitVal.getConstExps()) {
                fConstExp(constExp);
            }
        }
    }

    public void fConstExp(ConstExp constExp) {
        //  ConstExp → AddExp
        fAddExp(constExp.getAddExp());
    }

    public void fExp(Exp exp) {
        // Exp → AddExp
         fAddExp(exp.getAddExp());
    }

    public void fAddExp(AddExp addExp) {
        // AddExp → MulExp { ('+' | '-') MulExp }
        for (MulExp mulExp : addExp.getMulExps()) {
            fMulExp(mulExp);
        }
    }

    public void fMulExp(MulExp mulExp) {
        // MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
        for (UnaryExp unaryExp : mulExp.getUnaryExps()) {
            fUnaryExp(unaryExp);
        }
    }

    public void fUnaryExp(UnaryExp unaryExp) {
        //  UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (unaryExp.getPrimaryExp() != null) {
            fPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getIdent() != null) {
            if (!currentSymbolTable.get(unaryExp.getIdent().getContent())) {
                errors.add(new Error(unaryExp.getIdent().getLineNumber(), ErrorType.c));
            }
            if (unaryExp.getFuncRParams() != null) {
                fFuncRParams(unaryExp.getFuncRParams());
            }
        } else {
            fUnaryExp(unaryExp.getUnaryExp());
        }
    }

    public void fPrimaryExp(PrimaryExp primaryExp) {
        // PrimaryExp → '(' Exp ')' | LVal | Number | Character
        if (primaryExp.getExp() != null) {
            fExp(primaryExp.getExp());
        } else if (primaryExp.getLVal() != null) {
            fLVal(primaryExp.getLVal());
        }
    }

    public void fLVal(LVal lVal) {
        // LVal → Ident | Ident '[' Exp ']'
        Token ident = lVal.getIdent();
        if (!currentSymbolTable.get(ident.getContent())) {
            errors.add(new Error(ident.getLineNumber(), ErrorType.c));
        }
        if (lVal.getExp() != null) {
            fExp(lVal.getExp());
        }
    }


    public void fFuncRParams(FuncRParams funcFParams) {
        // FuncRParams → '(' [ Exp { ',' Exp } ] ')'
        for (Exp exp : funcFParams.getExps()) {
            fExp(exp);
        }
    }

    public void fFuncFParams(FuncFParams funcFParams) {
        // FuncFParams → '(' [ FuncFParam { ',' FuncFParam } ] ')'
        for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
            fFuncFParam(funcFParam);
        }
    }

    public void fFuncFParam(FuncFParam funcFParam) {
        // FuncFParam → BType Ident [ '[' ']' ]
        Token ident = funcFParam.getIdent();
        if (currentSymbolTable.contains(ident.getContent())) {
            errors.add(new Error(funcFParam.getIdent().getLineNumber(), ErrorType.b));
        }
         if (funcFParam.getDimension() == 0) {
            currentSymbolTable.put(funcFParam.getIdent().getContent(), SymbolFactory.buildVar0(funcFParam.getBType(), ident.getContent()));
        } else {
            currentSymbolTable.put(funcFParam.getIdent().getContent(), SymbolFactory.buildVar1(funcFParam.getBType(), ident.getContent()));
        }
    }

    public void fBlock(Block block) {
        // Block → '{' { BlockItem } '}'
        for (BlockItem blockItem : block.getBlockItems()) {
            fBlockItem(blockItem);
        }
    }

    public void fBlockItem(BlockItem blockItem) {
        // BlockItem → Decl | Stmt
        if (blockItem.getDecl() != null) {
            fDecl(blockItem.getDecl());
        } else {
            fStmt(blockItem.getStmt());
        }
    }

    public void fStmt(Stmt stmt) {
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
        switch (stmt.getType()) {
            case LValAssignExp:
                fLVal(stmt.getLVal());
                for (Exp exp : stmt.getExps()) {
                    fExp(exp);
                }
                break;
            case Exp:
                for (Exp exp : stmt.getExps()) {
                    fExp(exp);
                }
                break;
            case Block:
                SymbolTable parent = currentSymbolTable;
                currentSymbolTable = new SymbolTable(++index, parent);
                symbolTables.add(currentSymbolTable);
                fBlock(stmt.getBlock());
                currentSymbolTable = parent;
                break;
            case If:
                fCond(stmt.getCond());
                fStmt(stmt.getStmt());
                if (stmt.getStmtElse() != null) {
                    fStmt(stmt.getStmtElse());
                }
                break;
            case For:
                if (stmt.getForStmt1() != null) {
                    fForStmt(stmt.getForStmt1());
                }
                if (stmt.getCond() != null) {
                    fCond(stmt.getCond());
                }
                if (stmt.getForStmt2() != null) {
                    fForStmt(stmt.getForStmt2());
                }
                fStmt(stmt.getStmt());
                break;
            case Return:
                for (Exp exp : stmt.getExps()) {
                    fExp(exp);
                }
                break;
            case LValAssignGetint:
                fLVal(stmt.getLVal());
                break;
            case LValAssignGetchar:
                fLVal(stmt.getLVal());
                break;
            case Printf:
                for (Exp exp : stmt.getExps()) {
                    fExp(exp);
                }
                break;
        }
    }

    public void fForStmt(ForStmt forStmt) {
        // ForStmt → LVal '=' Exp
        fLVal(forStmt.getLVal());
        fExp(forStmt.getExp());
    }

    public void fCond(Cond cond) {
        // Cond → LOrExp
        fLOrExp(cond.getLOrExp());
    }

    public void fLOrExp(LOrExp lOrExp) {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        for (LAndExp lAndExp : lOrExp.getLAndExps()) {
            fLAndExp(lAndExp);
        }
    }

    public void fLAndExp(LAndExp lAndExp) {
        // LAndExp → EqExp | LAndExp '&&' EqExp
        for (EqExp eqExp : lAndExp.getEqExps()) {
            fEqExp(eqExp);
        }

    }

    public void fEqExp(EqExp eqExp) {
        //  EqExp → RelExp | EqExp ('==' | '!=') RelExp
        for (RelExp relExp : eqExp.getRelExps()) {
            fRelExp(relExp);
        }
    }

    public void fRelExp(RelExp relExp) {
        //  RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
         for (AddExp addExp : relExp.getAddExps()) {
            fAddExp(addExp);
         }
    }
}