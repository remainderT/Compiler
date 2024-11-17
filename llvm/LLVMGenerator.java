package llvm;

import common.TokenType;
import frontend.Token;
import llvm.types.FunctionType;
import llvm.types.IntegerType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.types.VoidType;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import llvm.values.constants.Function;
import llvm.values.constants.GlobalVar;
import llvm.values.instructions.Operator;
import syntaxNode.AddExp;
import syntaxNode.Block;
import syntaxNode.BlockItem;
import syntaxNode.CompUnit;
import syntaxNode.ConstDecl;
import syntaxNode.ConstDef;
import syntaxNode.ConstExp;
import syntaxNode.ConstInitVal;
import syntaxNode.Decl;
import syntaxNode.Exp;
import syntaxNode.FuncDef;
import syntaxNode.InitVal;
import syntaxNode.LVal;
import syntaxNode.MainFuncDef;
import syntaxNode.MulExp;
import syntaxNode.PrimaryExp;
import syntaxNode.Stmt;
import syntaxNode.UnaryExp;
import syntaxNode.VarDecl;
import syntaxNode.VarDef;
import util.ValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static common.StmtTpye.Block;
import static common.StmtTpye.Break;
import static common.StmtTpye.Continue;
import static common.StmtTpye.Exp;
import static common.StmtTpye.For;
import static common.StmtTpye.If;
import static common.StmtTpye.LValAssignExp;
import static common.StmtTpye.LValAssignGetchar;
import static common.StmtTpye.LValAssignGetint;
import static common.StmtTpye.Printf;
import static common.StmtTpye.Return;

public class LLVMGenerator {

    int index = 0;

    private final IRModule irModule = IRModule.getInstance();

    private final CompUnit compUnit;

    private List<LLVMSymbolTable> symbolTables;

    private LLVMSymbolTable currentSymbolTable;

    private Stack<Value> valueStack;

    private BasicBlock currentBlock;

    private Function currentFunction;

    private Boolean isGlobal;

    private Boolean isConst;

    private Type currentType;

    public LLVMGenerator(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void Generate() {
        gCompUnit(compUnit);
    }

    public void init() {
        isConst = true;
        isGlobal = true;
        valueStack = new Stack<>();
        symbolTables = new ArrayList<>();
        currentSymbolTable = new LLVMSymbolTable(index++, null);
        symbolTables.add(currentSymbolTable);


        Function getInt = new Function("getint", new FunctionType(IntegerType.I32, new ArrayList<>()), true);
        Function getChar = new Function("getchar", new FunctionType(IntegerType.I8, new ArrayList<>()), true);
        Function putInt = new Function("putint", new FunctionType(VoidType.Void, List.of(IntegerType.I32)), true);
        Function putCh = new Function("putch", new FunctionType(VoidType.Void, List.of(IntegerType.I32)), true);
        Function putStr = new Function("putstr", new FunctionType(VoidType.Void, List.of(new PointerType(IntegerType.I8))), true);
        irModule.addFunction(getInt);
        irModule.addFunction(getChar);
        irModule.addFunction(putInt);
        irModule.addFunction(putCh);
        irModule.addFunction(putStr);
        currentSymbolTable.put("getint", getInt);
        currentSymbolTable.put("getchar", getChar);
        currentSymbolTable.put("putint", putInt);
        currentSymbolTable.put("putch", putCh);
        currentSymbolTable.put("putstr", putStr);

    }

    public void gCompUnit(CompUnit compUnit) {
        init();
        // CompUnit -> {Decl} {FuncDef} MainFuncDef
        for (Decl decl : compUnit.getDecls()) {
            gDecl(decl);
        }

        isGlobal = false;
        isConst = false;

        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            gFuncDef(funcDef);
        }

        gMainFuncDef(compUnit.getMainFuncDef());
    }

    public void gDecl(Decl decl) {
        // Decl -> ConstDecl | VarDecl
        if (decl.getConstDecl() != null) {
            isConst = true;
            gConstDecl(decl.getConstDecl());
            isConst = false;
        } else {
            gVarDecl(decl.getVarDecl());
        }
    }

    public void gConstDecl(ConstDecl constDecl) {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        currentType = constDecl.getBType().getToken().getType() == TokenType.INTTK ? IntegerType.I32 : IntegerType.I8;
        for (ConstDef constDef : constDecl.getConstDefs()) {
            gConstDef(constDef);
        }
    }

    public void gConstDef(ConstDef constDef) {
        // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
        String name = constDef.getIdent().getContent();
        if (constDef.getDimension() == 0) {
            gConstInitVal(constDef.getConstInitVal());
            if (isGlobal) {
                Value value = !valueStack.empty() ? valueStack.pop() : null;
                if (value != null) {
                    value.setType(currentType);
                }
                GlobalVar globalVar = ValueFactory.getGlobalVar(name, currentType, isConst, value);
                irModule.addGlobalVar(globalVar);
                currentSymbolTable.put(name, globalVar);
            } else {
                Value value = !valueStack.empty() ? valueStack.pop() : null;
                Instruction inst1 = ValueFactory.getAllocaInst(currentBlock, isConst, currentType);
                currentBlock.addInstruction(inst1);
                if (value != null) {
                    if (currentType != value.getType()) {
                        Instruction inst3 = ValueFactory.getConvInst(currentBlock, value);
                        currentBlock.addInstruction(inst3);
                        value = inst3;
                    }
                    Instruction inst2 = ValueFactory.getStoreInst(value, inst1);
                    currentBlock.addInstruction(inst2);
                }
            }
        }
    }

    public void gConstInitVal(ConstInitVal constInitVal) {
        //  ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
        if (constInitVal.getConstExps() != null) {
            for (ConstExp constExp : constInitVal.getConstExps()) {
                gConstExp(constExp);
            }
        } else {
            // StringConst
        }
    }

    public void gConstExp(ConstExp constExp) {
        // ConstExp → AddExp
        gAddExp(constExp.getAddExp());
    }

    public void gVarDecl(VarDecl varDecl) {
        // VarDecl → BType VarDef { ',' VarDef } ';'
        currentType = varDecl.getBType().getToken().getType() == TokenType.INTTK ? IntegerType.I32 : IntegerType.I8;
        for (VarDef varDef : varDecl.getVarDefs()) {
            gVarDef(varDef);
        }
    }

    public void gVarDef(VarDef varDef) {
        // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
        String name = varDef.getIdent().getContent();
        if (varDef.getDimension() == 0) {
            if (isGlobal) {
                Value value = null;
                if (varDef.getInitVal() != null) {
                    gInitVal(varDef.getInitVal());
                    value = valueStack.pop();
                    value.setType(currentType);
                }
                GlobalVar globalVar = ValueFactory.getGlobalVar(name, currentType, isConst, value);
                irModule.addGlobalVar(globalVar);
                currentSymbolTable.put(name, globalVar);
            } else {
                Instruction inst1 = ValueFactory.getAllocaInst(currentBlock, isConst, currentType);
                currentBlock.addInstruction(inst1);
                currentSymbolTable.put(name, inst1);
                Value value = null;
                if (varDef.getInitVal() != null) {
                    gInitVal(varDef.getInitVal());
                    value =  valueStack.pop();
                    if (currentType != value.getType()) {
                        Instruction inst3 = ValueFactory.getConvInst(currentBlock, value);
                        currentBlock.addInstruction(inst3);
                        value = inst3;
                    }
                }
                Instruction inst2 = ValueFactory.getStoreInst(value, inst1);
                currentBlock.addInstruction(inst2);
            }
        }
    }

    public void gInitVal(InitVal initVal) {
        // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        if (initVal.getExps() != null) {
            for (Exp exp : initVal.getExps()) {
                gExp(exp);
            }
        } else {
            // StringConst
        }
    }

    public void gFuncDef(FuncDef funcDef) {
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        Type returnType = funcDef.getFuncType().getToken().getType() == TokenType.INTTK ? IntegerType.I32 : IntegerType.I8;
        List<Type> paramTypes = new ArrayList<>();
        Type functionType = new FunctionType(returnType, paramTypes);
        currentFunction = new Function(funcDef.getIdent().getContent(), functionType, false);
        irModule.addFunction(currentFunction);
        currentSymbolTable.put(funcDef.getIdent().getContent(), currentFunction);
        gBlock(funcDef.getBlock());
    }

    public void gMainFuncDef(MainFuncDef mainFuncDef) {
        // MainFuncDef → 'int' 'main' '(' ')' Block
        LLVMSymbolTable next = new LLVMSymbolTable(index++, currentSymbolTable);
        symbolTables.add(next);
        currentSymbolTable = next;

        currentFunction = new Function("main", new FunctionType(IntegerType.I32, new ArrayList<>()), false);
        irModule.addFunction(currentFunction);
        symbolTables.get(0).put("main", currentFunction);

        currentBlock = new BasicBlock("block", currentFunction.getRegNum());
        gBlock(mainFuncDef.getBlock());
        currentFunction.addBlock(currentBlock);

        currentSymbolTable = currentSymbolTable.getFather();
    }

    public void gBlock(Block block) {
        // Block -> {BlockItem}
        for (BlockItem blockItem : block.getBlockItems()) {
            gBlockItem(blockItem);
        }
    }

    public void gBlockItem(BlockItem blockItem) {
        // BlockItem -> Decl | Stmt
        if (blockItem.getDecl() != null) {
            gDecl(blockItem.getDecl());
        } else {
            gStmt(blockItem.getStmt());
        }
    }

    public void gStmt(Stmt stmt) {
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
        if (stmt.getType() == LValAssignExp ) {
            LVal lVal = stmt.getLVal();
            Exp exp = stmt.getExps().get(0);
            gExp(exp);
            Value value = valueStack.pop();
            Value lValValue = getValue(lVal.getIdent().getContent());
            Instruction inst = ValueFactory.getStoreInst(value, lValValue);
            currentBlock.addInstruction(inst);
        } else if (stmt.getType() == Exp) {


        } else if (stmt.getType() == Return) {
            Value value = null;
            if (!stmt.getExps().isEmpty()) {
                gExp(stmt.getExps().get(0));
                value = valueStack.pop();
            }
            Instruction inst = ValueFactory.getRetInst(value);
            currentBlock.addInstruction(inst);
        } else if (stmt.getType() == Block) {
            LLVMSymbolTable next = new LLVMSymbolTable(index++, currentSymbolTable);
            symbolTables.add(next);
            currentSymbolTable = next;

            gBlock(stmt.getBlock());

            currentSymbolTable = currentSymbolTable.getFather();
        } else if (stmt.getType() == If) {

        } else if (stmt.getType() == For) {

        } else if (stmt.getType() == Break || stmt.getType() == Continue) {

        } else if (stmt.getType() == LValAssignGetint || stmt.getType() == LValAssignGetchar) {

        } else if (stmt.getType() == Printf) {

        }
    }

    public void gExp(Exp exp) {
        // Exp → AddExp
        gAddExp(exp.getAddExp());
    }

    public void gAddExp(AddExp addExp) {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        for (int i = 0; i < addExp.getMulExps().size(); i++) {
            gMulExp(addExp.getMulExps().get(i));
            if (valueStack.size() >= 2) {
                Value right = valueStack.pop();
                Value left = valueStack.pop();
                if (isConst) {
                    int num1 = Integer.parseInt(left.getName());
                    int num2 = Integer.parseInt(right.getName());
                    int result = calculate(addExp.getOperations().get(i-1), num1, num2);
                    valueStack.add(ValueFactory.getIntConst(String.valueOf(result), false));
                } else {
                    Operator op = addExp.getOperations().get(i-1).getType() == TokenType.PLUS ? Operator.Add : Operator.Sub;
                    Instruction inst = ValueFactory.getBinaryInst(currentBlock, op, left, right);
                    currentBlock.addInstruction(inst);
                    valueStack.add(inst);
                }
            }
        }
    }

    public void gMulExp(MulExp mulExp) {
        // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        for (int i = 0; i < mulExp.getUnaryExps().size(); i++) {
            gUnaryExp(mulExp.getUnaryExps().get(i));
            if (valueStack.size() >= 2 && i > 0) {
                Value right = valueStack.pop();
                Value left = valueStack.pop();
                if (isConst) {
                    int num1 = Integer.parseInt(left.getName());
                    int num2 = Integer.parseInt(right.getName());
                    int result = calculate(mulExp.getOperators().get(i-1), num1, num2);
                    valueStack.add(ValueFactory.getIntConst(String.valueOf(result), false));
                } else {
                    Operator op = mulExp.getOperators().get(i - 1).getType() == TokenType.MULT ? Operator.Mul :
                            mulExp.getOperators().get(i - 1).getType() == TokenType.DIV ? Operator.Sdiv : Operator.Srem;
                    Instruction inst = ValueFactory.getBinaryInst(currentBlock, op, left, right);
                    currentBlock.addInstruction(inst);
                    valueStack.add(inst);
                }
            }
        }
    }

    public void gUnaryExp(UnaryExp unaryExp) {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (unaryExp.getPrimaryExp() != null) {
            gPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getIdent() != null) {

        } else {
            Operator op = unaryExp.getUnaryOp().getToken().getType() == TokenType.PLUS ? Operator.Add : Operator.Sub;
            Value left = ValueFactory.getIntConst("0", false);
            gUnaryExp(unaryExp.getUnaryExp());
            Value right = valueStack.pop();
            Instruction inst = ValueFactory.getBinaryInst(currentBlock, op, left, right);
            currentBlock.addInstruction(inst);
            valueStack.add(inst);
        }
    }

    public void gPrimaryExp(PrimaryExp primaryExp) {
        // PrimaryExp → '(' Exp ')' | LVal | Number | Character
        if (primaryExp.getExp() != null) {
            gExp(primaryExp.getExp());
        } else if (primaryExp.getLVal() != null) {
            gLVal(primaryExp.getLVal());
        } else if (primaryExp.getNumber() != null) {
            valueStack.add(ValueFactory.getIntConst(primaryExp.getNumber().getIntcon().getContent(), false));
        } else {
            valueStack.add(ValueFactory.getIntConst(primaryExp.getCharacter().getChrcon().getContent(), true));
        }
    }

    public void gCond() {
        // Cond → LOrExp
    }

    public void gLAndExp() {
        // LAndExp → EqExp | LAndExp '&&' EqExp
    }

    public void gEqExp() {
        // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    }

    public void gRelExp() {
        // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    }

    public void gLVal(LVal lVal) {
        // LVal → Ident ['[' Exp ']']
        if (lVal.getDimension() == 0) {
            Value value = getValue(lVal.getIdent().getContent());
            Instruction inst = ValueFactory.getLoadInst(currentBlock, value);
            valueStack.add(inst);
            currentBlock.addInstruction(inst);
        } else {
            // Array
        }
    }

    public void gFuncFParams() {
        // FuncFParams → FuncFParam { ',' FuncFParam }
    }

    public void gFuncFParam() {
        // FuncFParam → BType Ident ['[' ']']
    }

    public void gFuncRParams() {
        // FuncRParams → Exp { ',' Exp }
    }

    public int calculate(Token token, int num1, int num2) {
        if (token.getType() == TokenType.PLUS) {
            return num1 + num2;
        } else if (token.getType() == TokenType.MINU) {
            return num1 - num2;
        } else if (token.getType() == TokenType.MULT) {
            return num1 * num2;
        } else if (token.getType() == TokenType.DIV) {
            return num1 / num2;
        } else if (token.getType() == TokenType.MOD) {
            return num1 % num2;
        }
        return 0;
    }

    public Value getValue(String name) {
        LLVMSymbolTable tmp = currentSymbolTable;
        while (tmp != null) {
            Value value = tmp.get(name);
            if (value != null) {
                return value;
            }
            tmp = tmp.getFather();
        }
        return null;
    }

}
