package util;

import llvm.types.IntegerType;
import llvm.types.Type;
import llvm.values.Arguement;
import llvm.values.BasicBlock;
import llvm.values.Value;
import llvm.values.constants.Function;
import llvm.values.constants.GlobalArray;
import llvm.values.constants.GlobalVar;
import llvm.values.constants.IntConst;
import llvm.values.instructions.AllocaInst;
import llvm.values.instructions.BinaryInst;
import llvm.values.instructions.BrInst;
import llvm.values.instructions.CallInst;
import llvm.values.instructions.ConvInst;
import llvm.values.instructions.GepInst;
import llvm.values.instructions.LoadInst;
import llvm.values.instructions.Operator;
import llvm.values.instructions.RetInst;
import llvm.values.instructions.StoreInst;

import java.util.List;

public class ValueFactory {

    public static BasicBlock buildBasicBlock(String name) {
        return new BasicBlock(name);
    }

    public static IntConst buildIntConst(int value, Boolean isChar) {
        if (isChar) {
            return new IntConst(value, IntegerType.I8);
        } else {
            return new IntConst(value, IntegerType.I32);
        }
    }

    public static GlobalVar buildGlobalVar(String name, Type type, boolean isConst, Value value) {
        return new GlobalVar(name, type, isConst, value);
    }

    public static GlobalArray buildGlobalArray(String name, Boolean isConst, int capacity, Type elememtType) {
        return new GlobalArray(name, isConst, capacity, elememtType);
    }

    public static Arguement buildArguement(Type type, int index) {
        return new Arguement(type, index);
    }

    // instructions
    public static CallInst buildCallInst(BasicBlock basicBlock, Function function, List<Value> params) {
        CallInst inst = new CallInst(basicBlock, function, params);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static LoadInst buildLoadInst(BasicBlock basicBlock, Value value) {
        LoadInst inst = new LoadInst(basicBlock, value);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static void buildRetInst(BasicBlock basicBlock, Value value) {
        RetInst inst = new RetInst(value);
        basicBlock.addInstruction(inst);
    }

    public static ConvInst buildConvInst(BasicBlock basicBlock, Value value, IntegerType fromType, IntegerType toType) {
        Type from = value.getType();
        Operator op = from == IntegerType.I32 ? Operator.Trunc : Operator.Zext;
        ConvInst inst = new ConvInst(basicBlock, op, value, fromType, toType);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static BinaryInst buildBinaryInst(BasicBlock basicBlock, Operator op, Value left, Value right) {
        BinaryInst inst = new BinaryInst(basicBlock, op, left, right);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static AllocaInst buildAllocaInst(BasicBlock basicBlock, Type allocaType) {
        AllocaInst inst = new AllocaInst(basicBlock, allocaType);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static void buildStoreInst(BasicBlock basicBlock, Value value, Value addr) {
        StoreInst inst = new StoreInst(value, addr);
        basicBlock.addInstruction(inst);
    }

    public static GepInst buildGepInst(BasicBlock basicBlock, Value array, Value offset) {
        GepInst inst = new GepInst(basicBlock, array, offset);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static Value[] checkTypeConversion(BasicBlock basicBlock, Value left, Value right) {
        if (left.getType() != IntegerType.I32) {
            left = buildConvInst(basicBlock, left, (IntegerType) left.getType(), IntegerType.I32);
        }
        if (right.getType() != IntegerType.I32) {
            right = buildConvInst(basicBlock, right, (IntegerType) right.getType(), IntegerType.I32);
        }
        return new Value[]{left, right};
    }

    public static void buildBrInst(BasicBlock fromBlock, BasicBlock trueBlock, BasicBlock falseBlock, Value condition) {
        if (!(condition.getType() instanceof IntegerType && condition.getType() == IntegerType.I1)) {
            condition = buildBinaryInst(fromBlock, Operator.Ne, condition, new IntConst(0, IntegerType.I32));
        }
        BrInst inst = new BrInst(fromBlock, trueBlock, falseBlock, condition);
        fromBlock.addInstruction(inst);
    }

    public static void buildBrInst(BasicBlock fromBlock, BasicBlock trueBlock) {
        BrInst inst = new BrInst(fromBlock, trueBlock);
        fromBlock.addInstruction(inst);
    }

    public static int getChar2Int(String s) {
        if (s.charAt(1) != '\\') {
            return s.charAt(1);
        }
        if (s.charAt(2) == 'a') {
            return 7;
        } else if (s.charAt(2) == 'b') {
            return 8;
        } else if (s.charAt(2) == 't') {
            return 9;
        } else if (s.charAt(2) == 'n') {
            return 10;
        } else if (s.charAt(2) == 'v') {
            return 11;
        } else if (s.charAt(2) == 'f') {
            return 12;
        } else if (s.charAt(2) == '\"') {
            return 34;
        } else if (s.charAt(2) == '\'') {
            return 39;
        } else if (s.charAt(2) == '\\') {
            return 92;
        } else {
            return 0;
        }
    }

    public static int getChar2Int(String s,int i) {
        if (s.charAt(i) != '\\') {
            return s.charAt(i);
        }
        if (s.charAt(i + 1) == 'a') {
            return 7;
        } else if (s.charAt(i + 1) == 'b') {
            return 8;
        } else if (s.charAt(i + 1) == 't') {
            return 9;
        } else if (s.charAt(i + 1) == 'n') {
            return 10;
        } else if (s.charAt(i + 1) == 'v') {
            return 11;
        } else if (s.charAt(i + 1) == 'f') {
            return 12;
        } else if (s.charAt(i + 1) == '\"') {
            return 34;
        } else if (s.charAt(i + 1) == '\'') {
            return 39;
        } else if (s.charAt(i + 1) == '\\') {
            return 92;
        } else {
            return 0;
        }
    }

}
