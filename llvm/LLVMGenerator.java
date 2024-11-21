package llvm;

import common.TokenType;
import frontend.Token;
import llvm.types.*;
import llvm.values.Arguement;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import llvm.values.constants.Function;
import llvm.values.constants.GlobalArray;
import llvm.values.constants.GlobalVar;
import llvm.values.constants.IntConst;
import llvm.values.instructions.AllocaInst;
import llvm.values.instructions.Operator;
import syntaxNode.*;
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

    private BasicBlock ifTrueBlock;

    private BasicBlock ifFalseBlock;

    private BasicBlock nowForLoopEnd;

    private BasicBlock afterForBlock;

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
        isGlobal = true;
        isConst = false;
        valueStack = new Stack<>();
        symbolTables = new ArrayList<>();
        currentSymbolTable = new LLVMSymbolTable(index++, null);
        symbolTables.add(currentSymbolTable);

        Function getInt = new Function("getint", new FunctionType(IntegerType.I32, new ArrayList<>()), true);
        Function getChar = new Function("getchar", new FunctionType(IntegerType.I32, new ArrayList<>()), true);
        Function putInt = new Function("putint", new FunctionType(VoidType.Void, List.of(IntegerType.I32)), true);
        Function putCh = new Function("putch", new FunctionType(VoidType.Void, List.of(IntegerType.I32)), true);
        irModule.addFunction(getInt);
        irModule.addFunction(getChar);
        irModule.addFunction(putInt);
        irModule.addFunction(putCh);
        currentSymbolTable.put("getint", getInt);
        currentSymbolTable.put("getchar", getChar);
        currentSymbolTable.put("putint", putInt);
        currentSymbolTable.put("putch", putCh);

    }

    public void gCompUnit(CompUnit compUnit) {
        init();
        // CompUnit -> {Decl} {FuncDef} MainFuncDef
        for (Decl decl : compUnit.getDecls()) {
            gDecl(decl);
        }

        isGlobal = false;

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
            Value value = !valueStack.empty() ? valueStack.pop() : null;
            if (isGlobal) {
                if (value != null) {
                    value.setType(currentType);
                }
                GlobalVar globalVar = ValueFactory.buildGlobalVar(name, currentType, isConst, value);
                irModule.addGlobalValue(globalVar);
                currentSymbolTable.put(name, globalVar);
            } else {
                Instruction inst = ValueFactory.buildAllocaInst(currentBlock, currentType);
                currentSymbolTable.put(name, inst);
                if (value != null) {
                    if (currentType != value.getType()) {
                        value = ValueFactory.buildConvInst(currentBlock, value);
                    }
                    ValueFactory.buildStoreInst(currentBlock, value, inst);
                }
            }
        } else {
            if (isGlobal) {
                gConstExp(constDef.getConstExp());
                Value capacityValue = valueStack.pop();
                int capacity = Integer.parseInt(capacityValue.getName());
                GlobalArray globalArray = ValueFactory.buildGlobalArray(name, isConst, capacity, currentType);
                if (constDef.getConstInitVal().getStringConst() == null) {
                    for (int i = 0; i < constDef.getConstInitVal().getConstExps().size(); i++) {
                        gConstExp(constDef.getConstInitVal().getConstExps().get(i));
                        Value element = valueStack.pop();
                        globalArray.addElement(element);
                    }
                    for (int i = constDef.getConstInitVal().getConstExps().size(); i < capacity; i++) {
                        IntConst zero = ValueFactory.buildIntConst(0, false);
                        globalArray.addElement(zero);
                    }
                } else {
                    String str = constDef.getConstInitVal().getStringConst().getContent();
                    for (int i = 1; i < str.length() - 1; i++) {
                        int value = str.charAt(i);
                        IntConst element = ValueFactory.buildIntConst(value, true);
                        globalArray.addElement(element);
                    }
                    for (int i = str.length() - 2; i < capacity; i++) {
                        IntConst zero = ValueFactory.buildIntConst(0, true);
                        globalArray.addElement(zero);
                    }
                }
                irModule.addGlobalValue(globalArray);
                currentSymbolTable.put(name, globalArray);
            } else {
                gConstExp(constDef.getConstExp());
                Value capacityValue = valueStack.pop();
                int capacity = Integer.parseInt(capacityValue.getName());
                ArrayType arrayType = new ArrayType(currentType, capacity);
                Value array = ValueFactory.buildAllocaInst(currentBlock, arrayType);
                if (constDef.getConstInitVal().getStringConst() == null) {
                    for (int i = 0; i < constDef.getConstInitVal().getConstExps().size(); i++) {
                        gConstExp(constDef.getConstInitVal().getConstExps().get(i));
                        Value element = valueStack.pop();
                        Instruction inst = ValueFactory.buildGepInst(currentBlock, array, element);
                        ValueFactory.buildStoreInst(currentBlock, element, inst);
                        valueStack.add(inst);
                    }
                    for (int i = constDef.getConstInitVal().getConstExps().size(); i < capacity; i++) {
                        IntConst zero = ValueFactory.buildIntConst(0, false);
                        Instruction inst = ValueFactory.buildGepInst(currentBlock, array, zero);
                        ValueFactory.buildStoreInst(currentBlock, zero, inst);
                        valueStack.add(inst);
                    }
                } else {
                    String str = constDef.getConstInitVal().getStringConst().getContent();
                    str = str.substring(1, str.length() - 1);
                    for (int i = 0; i < str.length(); i++) {
                        IntConst element = ValueFactory.buildIntConst(str.charAt(i), true);
                        Value offset = ValueFactory.buildIntConst(i, false);
                        Instruction inst = ValueFactory.buildGepInst(currentBlock, array, offset);
                        ValueFactory.buildStoreInst(currentBlock, element, inst);
                        valueStack.add(inst);
                    }
                    for (int i = str.length(); i < capacity; i++) {
                        IntConst zero = ValueFactory.buildIntConst(0, false);
                        Value offset = ValueFactory.buildIntConst(i, false);
                        Instruction inst = ValueFactory.buildGepInst(currentBlock, array, offset);
                        ValueFactory.buildStoreInst(currentBlock, zero, inst);
                        valueStack.add(inst);
                    }
                }
                currentSymbolTable.put(name, array);
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
            // StringConst 的在数组部分处理
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
                Value value = ValueFactory.buildIntConst(0, false);
                if (varDef.getInitVal() != null) {
                    gInitVal(varDef.getInitVal());
                    value = valueStack.pop();
                    value.setType(currentType);
                }
                GlobalVar globalVar = ValueFactory.buildGlobalVar(name, currentType, isConst, value);
                irModule.addGlobalValue(globalVar);
                currentSymbolTable.put(name, globalVar);
            } else {
                Instruction inst = ValueFactory.buildAllocaInst(currentBlock, currentType);
                currentSymbolTable.put(name, inst);
                if (varDef.getInitVal() != null) {
                    gInitVal(varDef.getInitVal());
                    Value value =  valueStack.pop();
                    if (currentType != value.getType()) {
                        value = ValueFactory.buildConvInst(currentBlock, value);
                    }
                    ValueFactory.buildStoreInst(currentBlock, value, inst);
                }
            }
        } else {
            if (isGlobal) {
                gConstExp(varDef.getConstExp());
                Value capacityValue = valueStack.pop();
                int capacity = Integer.parseInt(capacityValue.getName());
                GlobalArray globalArray = ValueFactory.buildGlobalArray(name, isConst, capacity, currentType);
                if (varDef.getInitVal() != null && varDef.getInitVal().getStringConst() == null) {
                    for (int i = 0; i < varDef.getInitVal().getExps().size(); i++) {
                        gExp(varDef.getInitVal().getExps().get(i));
                        Value element = valueStack.pop();
                        globalArray.addElement(element);
                    }
                    for (int i = varDef.getInitVal().getExps().size(); i < capacity; i++) {
                        IntConst zero = ValueFactory.buildIntConst(0, false);
                        globalArray.addElement(zero);
                    }
                } else if (varDef.getInitVal() != null && varDef.getInitVal().getStringConst() != null) {
                    String str = varDef.getInitVal().getStringConst().getContent();
                    for (int i = 1; i < str.length() - 1; i++) {
                        int value = str.charAt(i);
                        IntConst element = ValueFactory.buildIntConst(value, true);
                        globalArray.addElement(element);
                    }
                    for (int i = str.length() - 2; i < capacity; i++) {
                        IntConst zero = ValueFactory.buildIntConst(0, true);
                        globalArray.addElement(zero);
                    }
                } else {
                    Boolean isChar = currentType == IntegerType.I8;
                    for (int i = 0; i < capacity; i++) {
                        IntConst zero = ValueFactory.buildIntConst(0, isChar);
                        globalArray.addElement(zero);
                    }
                }
                irModule.addGlobalValue(globalArray);
                currentSymbolTable.put(name, globalArray);
            } else {
                gConstExp(varDef.getConstExp());
                Value capacityValue = valueStack.pop();
                int capacity = Integer.parseInt(capacityValue.getName());
                ArrayType arrayType = new ArrayType(currentType, capacity);
                Value array = ValueFactory.buildAllocaInst(currentBlock, arrayType);
                if (varDef.getInitVal() != null) {
                    if (varDef.getInitVal().getStringConst() == null) {
                        for (int i = 0; i < varDef.getInitVal().getExps().size(); i++) {
                            gExp(varDef.getInitVal().getExps().get(i));
                            Value element = valueStack.pop();
                            if (currentType == IntegerType.I8 && element.getType() == IntegerType.I32) {
                                element = ValueFactory.buildConvInst(currentBlock, element);
                            }
                            Value offset = ValueFactory.buildIntConst(i, false);
                            Instruction inst = ValueFactory.buildGepInst(currentBlock, array, offset);
                            ValueFactory.buildStoreInst(currentBlock, element, inst);
                            valueStack.add(inst);
                        }
                        for (int i = varDef.getInitVal().getExps().size(); i < capacity; i++) {
                            IntConst zero = ValueFactory.buildIntConst(0, false);
                            Instruction inst = ValueFactory.buildGepInst(currentBlock, array, zero);
                            ValueFactory.buildStoreInst(currentBlock, zero, inst);
                            valueStack.add(inst);
                        }
                    } else {
                        String str = varDef.getInitVal().getStringConst().getContent();
                        str = str.substring(1, str.length() - 1);
                        for (int i = 0; i < str.length(); i++) {
                            IntConst element = ValueFactory.buildIntConst(str.charAt(i), true);
                            Value offset = ValueFactory.buildIntConst(i, false);
                            Instruction inst = ValueFactory.buildGepInst(currentBlock, array, offset);
                            ValueFactory.buildStoreInst(currentBlock, element, inst);
                            valueStack.add(inst);
                        }
                        for (int i = str.length(); i < capacity; i++) {
                            IntConst zero = ValueFactory.buildIntConst(0, false);
                            Instruction inst = ValueFactory.buildGepInst(currentBlock, array, zero);
                            ValueFactory.buildStoreInst(currentBlock, zero, inst);
                            valueStack.add(inst);
                        }
                    }
                }
                currentSymbolTable.put(name, array);
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
        LLVMSymbolTable next = new LLVMSymbolTable(index++, currentSymbolTable);
        symbolTables.add(next);
        currentSymbolTable = next;

        Type returnType = funcDef.getFuncType().getToken().getType() == TokenType.INTTK ? IntegerType.I32 :
                funcDef.getFuncType().getToken().getType() == TokenType.CHARTK ? IntegerType.I8 : VoidType.Void;
        List<Type> paramTypes = new ArrayList<>();
        List<Arguement> args = new ArrayList<>();
        gFuncFParams(funcDef.getFuncFParams(), paramTypes, args);
        Type functionType = new FunctionType(returnType, paramTypes);

        currentFunction = new Function(funcDef.getIdent().getContent(), functionType, false);
        irModule.addFunction(currentFunction);
        symbolTables.get(0).put(funcDef.getIdent().getContent(), currentFunction);

        currentBlock = ValueFactory.buildBasicBlock("block");
        currentFunction.addBlock(currentBlock);
        currentBlock.setLabelRegNum(args.size());
        for (int i= 0; i < args.size(); i++) {
            Arguement arguement = args.get(i);
            String name = funcDef.getFuncFParams().getFuncFParams().get(i).getIdent().getContent();
            AllocaInst inst = ValueFactory.buildAllocaInst(currentBlock, arguement.getType());
            ValueFactory.buildStoreInst(currentBlock, arguement, inst);
            currentSymbolTable.put(name, inst);
        }

        gBlock(funcDef.getBlock());

        currentSymbolTable = currentSymbolTable.getFather();
    }

    public void gFuncFParams(FuncFParams funcFParams, List<Type> paramTypes, List<Arguement> args) {
        // FuncFParams → FuncFParam { ',' FuncFParam }
        if (funcFParams == null) {
            return;
        }
        int index = 1;
        for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
            gFuncFParam(funcFParam, paramTypes, args, index++);
        }
    }

    public void gFuncFParam(FuncFParam funcFParam, List<Type> paramTypes, List<Arguement> args, int index) {
        // FuncFParam → BType Ident ['[' ']']
        Type type = funcFParam.getBType().getToken().getType() == TokenType.INTTK ? IntegerType.I32 : IntegerType.I8;
        if (funcFParam.getDimension() == 1) {
            type = new PointerType(type);
        }
        paramTypes.add(type);
        Arguement arguement = ValueFactory.buildArguement(type, index);
        args.add(arguement);
        currentSymbolTable.put(funcFParam.getIdent().getContent(), arguement);
    }

    public void gMainFuncDef(MainFuncDef mainFuncDef) {
        // MainFuncDef → 'int' 'main' '(' ')' Block
        LLVMSymbolTable next = new LLVMSymbolTable(index++, currentSymbolTable);
        symbolTables.add(next);
        currentSymbolTable = next;

        currentFunction = new Function("main", new FunctionType(IntegerType.I32, new ArrayList<>()), false);
        irModule.addFunction(currentFunction);
        symbolTables.get(0).put("main", currentFunction);

        currentBlock = ValueFactory.buildBasicBlock("mainBlock");
        currentFunction.addBlock(currentBlock);
        gBlock(mainFuncDef.getBlock());

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
            gLVal(stmt.getLVal());
            Value addr = valueStack.pop();
            gExp(stmt.getExps().get(0));
            Value value = valueStack.pop();
            if (value.getType() != addr.getType()) {
                value = ValueFactory.buildConvInst(currentBlock, value);
            }
            ValueFactory.buildStoreInst(currentBlock, value, addr);
        } else if (stmt.getType() == Exp) {
            if (!stmt.getExps().isEmpty()) {
                gExp(stmt.getExps().get(0));
                valueStack.pop();
            }
        } else if (stmt.getType() == Return) {
            Value value = null;
            if (!stmt.getExps().isEmpty()) {
                valueStack.clear();
                gExp(stmt.getExps().get(0));
                value = valueStack.pop();
                if (value.getType() != currentFunction.getReturnType()) {
                    value = ValueFactory.buildConvInst(currentBlock, value);
                }
            }
            ValueFactory.buildRetInst(currentBlock, value);
        } else if (stmt.getType() == Block) {
            LLVMSymbolTable next = new LLVMSymbolTable(index++, currentSymbolTable);
            symbolTables.add(next);
            currentSymbolTable = next;

            gBlock(stmt.getBlock());

            currentSymbolTable = currentSymbolTable.getFather();
        } else if (stmt.getType() == If) {
            BasicBlock condBlock = ValueFactory.buildBasicBlock("ifCond");
            ValueFactory.buildBrInst(currentBlock, condBlock);

            ifTrueBlock = ValueFactory.buildBasicBlock("ifTrue");
            BasicBlock recordIfTureBlock = ifTrueBlock;
            ifFalseBlock = ValueFactory.buildBasicBlock("ifFalse");
            BasicBlock recordIfFalseBlock = ifFalseBlock;

            condBlock.setLabelRegNum(currentBlock.getRegNum());
            currentFunction.addBlock(condBlock);
            currentBlock = condBlock;
            gCond(stmt.getCond());

            recordIfTureBlock.setLabelRegNum(currentBlock.getRegNum());
            currentFunction.addBlock(recordIfTureBlock);
            currentBlock = recordIfTureBlock;
            gStmt(stmt.getStmt());

            if (stmt.getStmtElse() != null) {
                BasicBlock afterIfBlock = ValueFactory.buildBasicBlock("afterIf");
                ValueFactory.buildBrInst(currentBlock, afterIfBlock);
                recordIfFalseBlock.setLabelRegNum(currentBlock.getRegNum());
                currentBlock = recordIfFalseBlock;
                currentFunction.addBlock(recordIfFalseBlock);
                gStmt(stmt.getStmtElse());

                ValueFactory.buildBrInst(currentBlock, afterIfBlock);
                afterIfBlock.setLabelRegNum(currentBlock.getRegNum());
                currentFunction.addBlock(afterIfBlock);
                currentBlock = afterIfBlock;
            } else {
                ValueFactory.buildBrInst(currentBlock, recordIfFalseBlock);
                recordIfFalseBlock.setLabelRegNum(currentBlock.getRegNum());
                currentFunction.addBlock(recordIfFalseBlock);
                currentBlock = recordIfFalseBlock;
            }
        } else if (stmt.getType() == For) {
            BasicBlock condLoopStart = ValueFactory.buildBasicBlock("condLoopStart");
            BasicBlock recordNowForBlock = ValueFactory.buildBasicBlock("forLoop");
            afterForBlock = ValueFactory.buildBasicBlock("afterFor");
            BasicBlock recordAfterForBlock = afterForBlock;
            BasicBlock condLoopEnd = ValueFactory.buildBasicBlock("condLoopEnd");
            nowForLoopEnd = condLoopEnd;

            ValueFactory.buildBrInst(currentBlock, condLoopStart);
            // 初始表达式
            condLoopStart.setLabelRegNum(currentBlock.getRegNum());
            currentFunction.addBlock(condLoopStart);
            currentBlock = condLoopStart;
            if (stmt.getForStmt1() != null) {
                gForStmt(stmt.getForStmt1());
            }
            ifTrueBlock = recordNowForBlock;
            ifFalseBlock = recordAfterForBlock;
            if (stmt.getCond() != null) {
                gCond(stmt.getCond());
            } else {
                ValueFactory.buildBrInst(currentBlock, recordNowForBlock);
            }

            // 循环体
            recordNowForBlock.setLabelRegNum(currentBlock.getRegNum());
            currentFunction.addBlock(recordNowForBlock);
            currentBlock = recordNowForBlock;
            gStmt(stmt.getStmt());
            ValueFactory.buildBrInst(currentBlock, condLoopEnd);

            condLoopEnd.setLabelRegNum(currentBlock.getRegNum());
            currentFunction.addBlock(condLoopEnd);
            currentBlock = condLoopEnd;
            if (stmt.getForStmt2() != null) {
                gForStmt(stmt.getForStmt2());
            }
            ifTrueBlock = recordNowForBlock;
            ifFalseBlock = recordAfterForBlock;
            if (stmt.getCond() != null) {
                gCond(stmt.getCond());
            } else {
                ValueFactory.buildBrInst(currentBlock, recordNowForBlock);
            }

            recordAfterForBlock.setLabelRegNum(currentBlock.getRegNum());
            currentFunction.addBlock(recordAfterForBlock);
            currentBlock = recordAfterForBlock;
        } else if (stmt.getType() == Break || stmt.getType() == Continue) {
            if (stmt.getType() == Break ) {
                ValueFactory.buildBrInst(currentBlock,afterForBlock);
            } else {
                ValueFactory.buildBrInst(currentBlock,nowForLoopEnd);
            }
        } else if (stmt.getType() == LValAssignGetint) {
            LVal lVal = stmt.getLVal();
            Value addr = getValue(lVal.getIdent().getContent());
            Instruction inst = ValueFactory.buildCallInst(currentBlock, (Function) getValue("getint"),new ArrayList<>());
            ValueFactory.buildStoreInst(currentBlock, inst, addr);
        } else if (stmt.getType() == LValAssignGetchar) {
            LVal lVal = stmt.getLVal();
            Value addr = getValue(lVal.getIdent().getContent());
            Instruction inst1 = ValueFactory.buildCallInst(currentBlock, (Function) getValue("getchar"),new ArrayList<>());
            Instruction inst2 = ValueFactory.buildConvInst(currentBlock, inst1);
            ValueFactory.buildStoreInst(currentBlock, inst2, addr);
        } else if (stmt.getType() == Printf) {
            String format = stmt.getStrcon().getContent();
            format = format.replace("\\n","\n");
            int paramIndex = 0;
            for (int i =1 ; i < format.length()-1; i++) {
                List<Value> params = new ArrayList<>();
                if (format.charAt(i) == '%' && (format.charAt(i+1) == 'c' || format.charAt(i+1) == 'd')) {
                    gExp(stmt.getExps().get(paramIndex++));
                    Value value = valueStack.pop();
                    if (value.getType() == IntegerType.I8) {
                        value = ValueFactory.buildConvInst(currentBlock, value);
                    }
                    params.add(value);
                    if (format.charAt(i+1) == 'd') {
                        ValueFactory.buildCallInst(currentBlock, (Function) getValue("putint"), params);
                    } else if (format.charAt(i+1) == 'c') {
                        ValueFactory.buildCallInst(currentBlock, (Function) getValue("putch"), params);
                    }
                    i++;
                } else {
                    Value value = ValueFactory.buildIntConst(format.charAt(i), false);
                    params.add(value);
                    ValueFactory.buildCallInst(currentBlock, (Function) getValue("putch"), params);
                }
            }
        }
    }

    public void gForStmt(ForStmt forStmt) {
        // ForStmt → LVal '=' Exp
        LVal lVal = forStmt.getLVal();
        Exp exp = forStmt.getExp();
        gExp(exp);
        Value value = valueStack.pop();
        Value addr = getValue(lVal.getIdent().getContent());
        if (value.getType() != addr.getType()) {
            value = ValueFactory.buildConvInst(currentBlock, value);
        }
        ValueFactory.buildStoreInst(currentBlock, value, addr);
    }

    public void gExp(Exp exp) {
        // Exp → AddExp
        gAddExp(exp.getAddExp());
    }

    public void gAddExp(AddExp addExp) {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        gMulExp(addExp.getMulExps().get(0));
        for (int i = 0; i < addExp.getOperations().size(); i++) {
            gMulExp(addExp.getMulExps().get(i+1));
            Value right = valueStack.pop();
            Value left = valueStack.pop();
            if (isConst || isGlobal) {
                int num1 = Integer.parseInt(left.getName());
                int num2 = Integer.parseInt(right.getName());
                int result = calculate(addExp.getOperations().get(i), num1, num2);
                valueStack.add(ValueFactory.buildIntConst(result, false));
            } else {
                Operator op = addExp.getOperations().get(i).getType() == TokenType.PLUS ? Operator.Add : Operator.Sub;
                Instruction inst = ValueFactory.buildBinaryInst(currentBlock, op, left, right);
                valueStack.add(inst);
            }
        }
    }

    public void gMulExp(MulExp mulExp) {
        // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        gUnaryExp(mulExp.getUnaryExps().get(0));
        for (int i = 0; i < mulExp.getOperators().size(); i++) {
            gUnaryExp(mulExp.getUnaryExps().get(i+1));
            Value right = valueStack.pop();
            Value left = valueStack.pop();
            if (isConst || isGlobal) {
                int num1 = Integer.parseInt(left.getName());
                int num2 = Integer.parseInt(right.getName());
                int result = calculate(mulExp.getOperators().get(i), num1, num2);
                valueStack.add(ValueFactory.buildIntConst(result, false));
            } else {
                Operator op = mulExp.getOperators().get(i).getType() == TokenType.MULT ? Operator.Mul :
                        mulExp.getOperators().get(i).getType() == TokenType.DIV ? Operator.Sdiv : Operator.Srem;
                Instruction inst = ValueFactory.buildBinaryInst(currentBlock, op, left, right);
                valueStack.add(inst);
            }
        }
    }

    public void gUnaryExp(UnaryExp unaryExp) {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        if (unaryExp.getPrimaryExp() != null) {
            gPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getIdent() != null) {
            Function function = ((Function) getValue(unaryExp.getIdent().getContent()));
            ArrayList<Value> params = new ArrayList<>();
            gFuncRParams(unaryExp.getFuncRParams(), params);
            Instruction inst = ValueFactory.buildCallInst(currentBlock, function, params);
            if (function.getType() != VoidType.Void) {
                valueStack.add(inst);
            }
        } else {
            gUnaryExp(unaryExp.getUnaryExp());
            if (isGlobal || isConst) {
                int num = Integer.parseInt(valueStack.pop().getName());
                int result = unaryExp.getUnaryOp().getToken().getType() == TokenType.MINU ? -num : num;
                valueStack.add(ValueFactory.buildIntConst(result, false));
            } else {
                if (unaryExp.getUnaryOp().getToken().getType() == TokenType.MINU) {
                    Value value = valueStack.pop();
                    Value zero = ValueFactory.buildIntConst(0, false);
                    Instruction inst = ValueFactory.buildBinaryInst(currentBlock, Operator.Sub, zero, value);
                    valueStack.add(inst);
                } else if (unaryExp.getUnaryOp().getToken().getType() == TokenType.NOT) {
                    Value value = valueStack.pop();
                    Instruction inst = ValueFactory.buildBinaryInst(currentBlock, Operator.Eq, value, ValueFactory.buildIntConst(0, false));
                    valueStack.add(inst);
                }
            }
        }
    }

    public void gFuncRParams(FuncRParams funcRParams, ArrayList<Value> params) {
        // FuncRParams → Exp { ',' Exp }
        for (int i = 0;i < funcRParams.getExps().size(); i++) {
            Exp exp = funcRParams.getExps().get(i);
            gExp(exp);
            Value value = valueStack.pop();
            params.add(value);
        }
    }

    public void gPrimaryExp(PrimaryExp primaryExp) {
        // PrimaryExp → '(' Exp ')' | LVal | Number | Character
        if (primaryExp.getExp() != null) {
            gExp(primaryExp.getExp());
        } else if (primaryExp.getLVal() != null) {
            gLVal(primaryExp.getLVal());
            Value inst1 = valueStack.pop();
            Instruction inst2 = ValueFactory.buildLoadInst(currentBlock, inst1);
            valueStack.add(inst2);
        } else if (primaryExp.getNumber() != null) {
            int value = Integer.parseInt(primaryExp.getNumber().getIntcon().getContent());
            valueStack.add(ValueFactory.buildIntConst(value, false));
        } else {
            int value = primaryExp.getCharacter().getChrcon().getContent().charAt(1);    // "'c'"
            valueStack.add(ValueFactory.buildIntConst(value, true));
        }
    }

    public void gCond(Cond cond) {
        // Cond → LOrExp
        gLOrExp(cond.getLOrExp());
    }

    public void gLOrExp(LOrExp lOrExp) {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        BasicBlock wholeFalseBlock = ifFalseBlock;
        for (int i = 0; i < lOrExp.getLAndExps().size(); i++) {
            BasicBlock leftFalseBlock = ifFalseBlock;
            if (i != lOrExp.getLAndExps().size() - 1) {
                leftFalseBlock = ValueFactory.buildBasicBlock("ifFalse");
                ifFalseBlock = leftFalseBlock;
            }
            gLAndExp(lOrExp.getLAndExps().get(i));
            ifFalseBlock = wholeFalseBlock;
            if (i != lOrExp.getLAndExps().size() - 1) {
                leftFalseBlock.setLabelRegNum(currentBlock.getRegNum());
                currentFunction.addBlock(leftFalseBlock);
                currentBlock = leftFalseBlock;
            }
        }

    }

    public void gLAndExp(LAndExp lAndExp) {
        // LAndExp → EqExp | LAndExp '&&' EqExp
        BasicBlock wholeTureBlock = ifTrueBlock;
        for (int i = 0; i < lAndExp.getEqExps().size(); i++) {
            BasicBlock leftTrueBlock = ifTrueBlock;
            if (i != lAndExp.getEqExps().size() - 1) {
                leftTrueBlock = ValueFactory.buildBasicBlock("ifTrue");
                ifTrueBlock = leftTrueBlock;
            }
            valueStack.clear();
            gEqExp(lAndExp.getEqExps().get(i));
            Value value = valueStack.pop();
            ValueFactory.buildBrInst(currentBlock, leftTrueBlock, ifFalseBlock, value);
            ifTrueBlock = wholeTureBlock;
            if (i != lAndExp.getEqExps().size() - 1) {
                leftTrueBlock.setLabelRegNum(currentBlock.getRegNum());
                currentFunction.addBlock(leftTrueBlock);
                currentBlock = leftTrueBlock;
            }
        }
    }

    public void gEqExp(EqExp eqExp) {
        // EqExp → RelExp | EqExp ('==' | '!=') RelExp
        gRelExp(eqExp.getRelExps().get(0));
        for (int i=0; i < eqExp.getTokens().size(); i++) {
            gRelExp(eqExp.getRelExps().get(i+1));
            Value right = valueStack.pop();
            Value left = valueStack.pop();
            Operator op = eqExp.getTokens().get(i).getType() == TokenType.EQL ? Operator.Eq : Operator.Ne;
            Instruction inst = ValueFactory.buildBinaryInst(currentBlock, op, left, right);
            valueStack.add(inst);
        }
    }

    public void gRelExp(RelExp relExp) {
        // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        gAddExp(relExp.getAddExps().get(0));
        for (int i=0; i < relExp.getTokens().size(); i++) {
            gAddExp(relExp.getAddExps().get(i+1));
            Value right = valueStack.pop();
            Value left = valueStack.pop();
            Operator op = null;
            if (relExp.getTokens().get(i).getType() == TokenType.LSS) {
                op = Operator.Slt;
            } else if (relExp.getTokens().get(i).getType() == TokenType.LEQ) {
                op = Operator.Sle;
            } else if (relExp.getTokens().get(i).getType() == TokenType.GRE) {
                op = Operator.Sgt;
            } else if (relExp.getTokens().get(i).getType() == TokenType.GEQ) {
                op = Operator.Sge;
            }
            Instruction inst = ValueFactory.buildBinaryInst(currentBlock, op, left, right);
            valueStack.add(inst);
        }
    }

    public void gLVal(LVal lVal) {
        // LVal → Ident ['[' Exp ']']
        if (isConst || isGlobal) {
            Value value = getValue(lVal.getIdent().getContent());
            value = ValueFactory.buildIntConst(((GlobalVar) value).getVal(), false);
            valueStack.add(value);
        }
        else {
            if (lVal.getDimension() == 0) {
                Value value = getValue(lVal.getIdent().getContent());
                if (value.getType() instanceof ArrayType) {
                    value= ValueFactory.buildGepInst(currentBlock, value, ValueFactory.buildIntConst(0, false));
                    value.setType(new PointerType(value.getType()));
                }
                valueStack.add(value);
            } else {
                gAddExp(lVal.getExp().getAddExp());
                Value array = getValue(lVal.getIdent().getContent());
                if (array.getType() instanceof PointerType) {
                    array = ValueFactory.buildLoadInst(currentBlock, array);
                }
                Value value = valueStack.pop();
                value = ValueFactory.buildGepInst(currentBlock, array, value);
                valueStack.add(value);
            }
        }
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
