package frontend;

import common.Error;
import common.ErrorType;
import common.ParamType;
import common.SemanticType;
import symbol.FuncSymbolTable;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Semantic {

    private SymbolTable currentSymbolTable;

    private List<SymbolTable> symbolTables;

    private List<Error> errors;

    private int index;  // 符号表编号

    private int loopCount;

    private static final HashMap<SemanticType, ParamType> typeChangeMap = new HashMap<>();

    private CompUnit compUnit;

    static {
        typeChangeMap.put(SemanticType.Int, ParamType.Var);
        typeChangeMap.put(SemanticType.Char, ParamType.Var);
        typeChangeMap.put(SemanticType.ConstInt, ParamType.Var);
        typeChangeMap.put(SemanticType.ConstChar, ParamType.Var);
        typeChangeMap.put(SemanticType.CharArray, ParamType.CharArray);
        typeChangeMap.put(SemanticType.IntArray, ParamType.IntArray);
        typeChangeMap.put(SemanticType.ConstCharArray, ParamType.CharArray);
        typeChangeMap.put(SemanticType.ConstIntArray, ParamType.IntArray);
        typeChangeMap.put(SemanticType.CharFunc, ParamType.Var);
        typeChangeMap.put(SemanticType.IntFunc, ParamType.Var);
        typeChangeMap.put(SemanticType.VoidFunc, ParamType.Error);
    }

    public Semantic(CompUnit compUnit, List<Error> errors) {
        index = 1;
        loopCount = 0;
        symbolTables = new ArrayList<>();
        this.currentSymbolTable = new SymbolTable(1, null);
        symbolTables.add(currentSymbolTable);
        this.errors = errors;
        this.compUnit = compUnit;
    }

    public List<SymbolTable> getSymbolTables() {
        return symbolTables;
    }

    public CompUnit getCompUnit() {
        return compUnit;
    }

    public void fCompUnit() {
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
        if (currentSymbolTable.currentContains(ident.getContent())) {
            errors.add(new Error(ident.getLineNumber(), ErrorType.b));
        } else {
            currentSymbolTable.put(ident.getContent(), SymbolFactory.buildFunc(funcDef.getFuncType(), ident.getContent(), funcDef.getFuncFParams()));
        }
        SymbolTable parent = currentSymbolTable;
        currentSymbolTable = new FuncSymbolTable(++index, parent, funcDef.getFuncType().getToken());
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
        currentSymbolTable = new FuncSymbolTable(++index, parent, mainFuncDef.getInttk());
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
        if (currentSymbolTable.currentContains(ident.getContent())) {
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
        if (currentSymbolTable.currentContains(ident.getContent())) {
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
            if (!currentSymbolTable.parentContains(unaryExp.getIdent().getContent())) {
                errors.add(new Error(unaryExp.getIdent().getLineNumber(), ErrorType.c));
                return;
            }
            Symbol funcSymbol = currentSymbolTable.get(unaryExp.getIdent().getContent());
            if (unaryExp.getFuncRParams() == null && !funcSymbol.getParamTypes().isEmpty()) {
                errors.add(new Error(unaryExp.getIdent().getLineNumber(), ErrorType.d));
                return;
            }
            if (unaryExp.getFuncRParams() != null) {
                if (funcSymbol.getParamTypes().size() != unaryExp.getFuncRParams().getExps().size()) {
                    errors.add(new Error(unaryExp.getIdent().getLineNumber(), ErrorType.d));
                    return;
                }
                for (int i=0; i<funcSymbol.getParamTypes().size(); i++) {
                    SemanticType fpType = funcSymbol.getParamTypes().get(i);
                    Exp exp = unaryExp.getFuncRParams().getExps().get(i);
                    if (!checkParamTypeInExp(exp, typeChangeMap.get(fpType))) {
                        errors.add(new Error(unaryExp.getIdent().getLineNumber(), ErrorType.e));
                        return;
                    }
                }
                fFuncRParams(unaryExp.getFuncRParams());
            }
        } else {
            fUnaryExp(unaryExp.getUnaryExp());
        }
    }

    public Boolean checkParamTypeInExp(Exp exp, ParamType fpType) {
        return checkParamTypeInAddExp(exp.getAddExp(), fpType);
    }

    public Boolean checkParamTypeInAddExp(AddExp addExp, ParamType fpType) {
        for (MulExp mulExp : addExp.getMulExps()) {
            if (!checkParamTypeInMulExp(mulExp, fpType)) {
                return false;
            }
        }
        return true;
    }

    public Boolean checkParamTypeInMulExp(MulExp mulExp, ParamType fpType) {
        for (UnaryExp unaryExp : mulExp.getUnaryExps()) {
            if (!checkParamTypeInUnaryExp(unaryExp, fpType)) {
                return false;
            }
        }
        return true;
    }

    public Boolean checkParamTypeInUnaryExp(UnaryExp unaryExp, ParamType fpType) {
        if (unaryExp.getPrimaryExp() != null) {
            return checkParamTypeInPrimaryExp(unaryExp.getPrimaryExp(), fpType);
        } else if (unaryExp.getIdent() != null) {
            Symbol rpSymbol = currentSymbolTable.get(unaryExp.getIdent().getContent());
            return typeChangeMap.get(rpSymbol.getType()) == fpType;
        } else {
            return checkParamTypeInUnaryExp(unaryExp.getUnaryExp(), fpType);
        }
    }

    public Boolean checkParamTypeInPrimaryExp(PrimaryExp primaryExp, ParamType fpType) {
        if (primaryExp.getExp() != null) {
            return checkParamTypeInExp(primaryExp.getExp(), fpType);
        } else if (primaryExp.getLVal() != null) {
            return checkParamTypeInLVal(primaryExp.getLVal(), fpType);
        } else {
            return fpType == ParamType.Var;
        }
    }

    public Boolean checkParamTypeInLVal(LVal lVal, ParamType fpType) {
        Symbol symbol = currentSymbolTable.get(lVal.getIdent().getContent());
        if (typeChangeMap.get(symbol.getType()) == ParamType.Var) {
            return fpType == ParamType.Var;
        } else if (typeChangeMap.get(symbol.getType()) == ParamType.IntArray) {
            if (lVal.getLbrack() != null) {
                return fpType == ParamType.Var;
            } else {
                return fpType == ParamType.IntArray;
            }
        } else if (typeChangeMap.get(symbol.getType()) == ParamType.CharArray) {
            if (lVal.getLbrack() != null) {
                return fpType == ParamType.Var;
            } else {
                return fpType == ParamType.CharArray;
            }
        } else {
            return false;
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
        if (!currentSymbolTable.parentContains(ident.getContent())) {
            System.out.println(ident.getContent());
            currentSymbolTable.parentContains(ident.getContent());
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
        if (currentSymbolTable.currentContains(ident.getContent())) {
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
        if (currentSymbolTable instanceof FuncSymbolTable funcSymbolTable) {
            if (!block.getBlockItems().isEmpty()) {
                if (funcSymbolTable.getReturnType() == 0 || funcSymbolTable.getReturnType() == 1) {
                    if (block.getBlockItems().get(block.getBlockItems().size() - 1).getStmt() == null) {
                        errors.add(new Error(block.getRbrace().getLineNumber(), ErrorType.g));
                    } else if (block.getBlockItems().get(block.getBlockItems().size() - 1).getStmt().getReturntk() == null) {
                        errors.add(new Error(block.getRbrace().getLineNumber(), ErrorType.g));
                    }
                }
            } else {
                if (funcSymbolTable.getReturnType() == 0 || funcSymbolTable.getReturnType() == 1) {
                    errors.add(new Error(block.getRbrace().getLineNumber(), ErrorType.g));
                }
            }
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
        /*    Stmt → LVal '=' Exp ';'
            | [Exp] ';'
            | Block
            | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            ForStmt与Cond中缺省一个，3种情况 3. ForStmt与Cond中缺省两个，3种情况 4. ForStmt与Cond全部
            缺省，1种情况
            | 'break' ';' | 'continue' ';'
            | 'return' [Exp] ';'
            | LVal '=' 'getint''('')'';'
            | LVal '=' 'getchar''('')'';'
            | 'printf''('StringConst {','Exp}')'';'
*/
        switch (stmt.getType()) {
            case LValAssignExp:
                Symbol symbol = currentSymbolTable.get(stmt.getLVal().getIdent().getContent());
                if (symbol != null && symbol.isConst()) {
                    errors.add(new Error(stmt.getLVal().getIdent().getLineNumber(), ErrorType.h));
                }
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
            case Return:
                SymbolTable father = getAncestorSymbolTable();
                FuncSymbolTable funcSymbolTable = (FuncSymbolTable) father;
                if (funcSymbolTable.getReturnType() == 2 && !stmt.getExps().isEmpty()) {    // void
                    errors.add(new Error(stmt.getReturntk().getLineNumber(), ErrorType.f));
                }
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
                loopCount++;
                fStmt(stmt.getStmt());
                loopCount--;
                break;
            case Break, Continue:
                if (loopCount == 0) {
                    errors.add(new Error(stmt.getBreakOrcontinuetk().getLineNumber(), ErrorType.m));
                }
                break;
            case LValAssignGetint, LValAssignGetchar:
                Symbol symbol1 = currentSymbolTable.get(stmt.getLVal().getIdent().getContent());
                if (symbol1.isConst()) {
                    errors.add(new Error(stmt.getLVal().getIdent().getLineNumber(), ErrorType.h));
                }
                fLVal(stmt.getLVal());
                break;
            case Printf:
                Token strcon = stmt.getStrcon();
                String content = strcon.getContent();
                int totalCount = countFormat(content, "%d") + countFormat(content, "%c");
                if (stmt.getExps().size() != totalCount) {
                    errors.add(new Error(stmt.getPrinttk().getLineNumber(), ErrorType.l));
                }
                for (Exp exp : stmt.getExps()) {
                    fExp(exp);
                }
                break;
        }
    }

    public void fForStmt(ForStmt forStmt) {
        // ForStmt → LVal '=' Exp
        Symbol symbol = currentSymbolTable.get(forStmt.getLVal().getIdent().getContent());
        if (symbol.isConst()) {
            errors.add(new Error(forStmt.getLVal().getIdent().getLineNumber(), ErrorType.h));
        }
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

    public static int countFormat(String input, String format) {
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(input);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public SymbolTable getAncestorSymbolTable() {
        SymbolTable temp = currentSymbolTable;
        while (!(temp instanceof FuncSymbolTable)) {
            temp = temp.getParent();
        }
        return temp;
    }
}
