package llvm;

import common.TokenType;
import frontend.Token;
import llvm.types.FunctionType;
import llvm.types.IntegerType;
import llvm.types.Type;
import llvm.types.VoidType;
import llvm.values.Arguement;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import llvm.values.constants.Function;
import llvm.values.constants.GlobalVar;
import llvm.values.instructions.AllocaInst;
import llvm.values.instructions.Operator;
import syntaxNode.AddExp;
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
import syntaxNode.FuncDef;
import syntaxNode.FuncFParam;
import syntaxNode.FuncFParams;
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
            Value value = !valueStack.empty() ? valueStack.pop() : null;
            if (isGlobal) {
                if (value != null) {
                    value.setType(currentType);
                }
                GlobalVar globalVar = ValueFactory.buildGlobalVar(name, currentType, isConst, value);
                irModule.addGlobalVar(globalVar);
                currentSymbolTable.put(name, globalVar);
            } else {
                Instruction inst = ValueFactory.buildAllocaInst(currentBlock, isConst, currentType);
                currentSymbolTable.put(name, inst);
                if (value != null) {
                    if (currentType != value.getType()) {
                        value = ValueFactory.buildConvInst(currentBlock, value);
                    }
                    ValueFactory.buildStoreInst(currentBlock, value, inst);
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
                GlobalVar globalVar = ValueFactory.buildGlobalVar(name, currentType, isConst, value);
                irModule.addGlobalVar(globalVar);
                currentSymbolTable.put(name, globalVar);
            } else {
                Instruction inst = ValueFactory.buildAllocaInst(currentBlock, isConst, currentType);
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
        symbolTables.getFirst().put(funcDef.getIdent().getContent(), currentFunction);

        currentBlock = ValueFactory.buildBasicBlock("block");
        currentFunction.addBlock(currentBlock);
        currentBlock.setLabelRegNum(args.size());
        for (int i= 0; i < args.size(); i++) {
            Arguement arguement = args.get(i);
            String name = funcDef.getFuncFParams().getFuncFParams().get(i).getIdent().getContent();
            AllocaInst inst = ValueFactory.buildAllocaInst(currentBlock, false, arguement.getType());
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
        symbolTables.getFirst().put("main", currentFunction);

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
            LVal lVal = stmt.getLVal();
            Exp exp = stmt.getExps().getFirst();
            gExp(exp);
            Value value = valueStack.pop();
            Value addr = getValue(lVal.getIdent().getContent());
            if (value.getType() != addr.getType()) {
                value = ValueFactory.buildConvInst(currentBlock, value);
            }
            ValueFactory.buildStoreInst(currentBlock, value, addr);
        } else if (stmt.getType() == Exp) {
            if (!stmt.getExps().isEmpty()) {
                gExp(stmt.getExps().getFirst());
                valueStack.pop();
            }
        } else if (stmt.getType() == Return) {
            Value value = null;
            if (!stmt.getExps().isEmpty()) {
                valueStack.clear();
                gExp(stmt.getExps().getFirst());
                value = valueStack.pop();
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

        } else if (stmt.getType() == Break || stmt.getType() == Continue) {

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
                        Instruction inst1 = ValueFactory.buildConvInst(currentBlock, value);
                        currentBlock.addInstruction(inst1);
                        value = inst1;
                    }
                    params.add(value);
                    if (format.charAt(i+1) == 'd') {
                        ValueFactory.buildCallInst(currentBlock, (Function) getValue("putint"), params);
                    } else if (format.charAt(i+1) == 'c') {
                        ValueFactory.buildCallInst(currentBlock, (Function) getValue("putch"), params);
                    }
                    i++;
                } else {
                    Value value = ValueFactory.buildIntConst(String.valueOf(format.charAt(i)), true);
                    params.add(value);
                    ValueFactory.buildCallInst(currentBlock, (Function) getValue("putch"), params);
                }
            }
        }
    }

    public void gExp(Exp exp) {
        // Exp → AddExp
        gAddExp(exp.getAddExp());
    }


    public void gAddExp(AddExp addExp) {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        gMulExp(addExp.getMulExps().getFirst());
        for (int i = 0; i < addExp.getOperations().size(); i++) {
            gMulExp(addExp.getMulExps().get(i+1));
            Value right = valueStack.pop();
            Value left = valueStack.pop();
            if (isConst) {
                int num1 = Integer.parseInt(left.getName());
                int num2 = Integer.parseInt(right.getName());
                int result = calculate(addExp.getOperations().get(i), num1, num2);
                valueStack.add(ValueFactory.buildIntConst(String.valueOf(result), false));
            } else {
                Operator op = addExp.getOperations().get(i).getType() == TokenType.PLUS ? Operator.Add : Operator.Sub;
                Instruction inst = ValueFactory.buildBinaryInst(currentBlock, op, left, right);
                valueStack.add(inst);
            }
        }
    }

    public void gMulExp(MulExp mulExp) {
        // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        gUnaryExp(mulExp.getUnaryExps().getFirst());
        for (int i = 0; i < mulExp.getOperators().size(); i++) {
            gUnaryExp(mulExp.getUnaryExps().get(i+1));
            Value right = valueStack.pop();
            Value left = valueStack.pop();
            if (isConst) {
                int num1 = Integer.parseInt(left.getName());
                int num2 = Integer.parseInt(right.getName());
                int result = calculate(mulExp.getOperators().get(i), num1, num2);
                valueStack.add(ValueFactory.buildIntConst(String.valueOf(result), false));
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
            for (int i = 0;i < unaryExp.getFuncRParams().getExps().size(); i++) {
                Exp exp = unaryExp.getFuncRParams().getExps().get(i);
                gExp(exp);
                Value value = valueStack.pop();
                params.add(value);
            }
            Instruction inst = ValueFactory.buildCallInst(currentBlock, function, params);
            if (function.getType() != VoidType.Void) {
                valueStack.add(inst);
            }
        } else {
            Operator op = unaryExp.getUnaryOp().getToken().getType() == TokenType.PLUS ? Operator.Add : Operator.Sub;
            Value left = ValueFactory.buildIntConst("0", false);
            gUnaryExp(unaryExp.getUnaryExp());
            Value right = valueStack.pop();
            Instruction inst = ValueFactory.buildBinaryInst(currentBlock, op, left, right);
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
            valueStack.add(ValueFactory.buildIntConst(primaryExp.getNumber().getIntcon().getContent(), false));
        } else {
            String number = primaryExp.getCharacter().getChrcon().getContent().substring(1);   // "'c'"
            valueStack.add(ValueFactory.buildIntConst(number, true));
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
        gRelExp(eqExp.getRelExps().getFirst());
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
        gAddExp(relExp.getAddExps().getFirst());
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
        if (lVal.getDimension() == 0) {
            Value value = getValue(lVal.getIdent().getContent());
            Instruction inst = ValueFactory.buildLoadInst(currentBlock, value);
            valueStack.add(inst);
        } else {
            // Array
        }
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
