package util;

import llvm.types.IntegerType;
import llvm.types.Type;
import llvm.values.Arguement;
import llvm.values.BasicBlock;
import llvm.values.Value;
import llvm.values.constants.Function;
import llvm.values.constants.GlobalVar;
import llvm.values.constants.IntConst;
import llvm.values.instructions.AllocaInst;
import llvm.values.instructions.BinaryInst;
import llvm.values.instructions.BrInst;
import llvm.values.instructions.CallInst;
import llvm.values.instructions.ConvInst;
import llvm.values.instructions.LoadInst;
import llvm.values.instructions.Operator;
import llvm.values.instructions.RetInst;
import llvm.values.instructions.StoreInst;

import java.util.List;

public class ValueFactory {

    public static BasicBlock buildBasicBlock(String name) {
        return new BasicBlock(name);
    }

    public static IntConst buildIntConst(String number, Boolean isChar) {
        if (isChar) {
            return new IntConst(number.charAt(0), IntegerType.I8);
        } else {
            return new IntConst(Integer.parseInt(number), IntegerType.I32);
        }
    }

    public static GlobalVar buildGlobalVar(String name, Type type, boolean isConst, Value value) {
        return new GlobalVar(name, type, isConst, value);
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

    public static ConvInst buildConvInst(BasicBlock basicBlock, Value value) {
        Type from = value.getType();
        Operator op = from == IntegerType.I32 ? Operator.Trunc : Operator.Zext;
        ConvInst inst = new ConvInst(basicBlock, op, value);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static BinaryInst buildBinaryInst(BasicBlock basicBlock, Operator op, Value left, Value right) {
        BinaryInst inst = new BinaryInst(basicBlock, op, left, right);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static AllocaInst buildAllocaInst(BasicBlock basicBlock, boolean isConst, Type allocaType) {
        AllocaInst inst = new AllocaInst(basicBlock, isConst, allocaType);
        basicBlock.addInstruction(inst);
        return inst;
    }

    public static void buildStoreInst(BasicBlock basicBlock, Value value, Value pointer) {
        StoreInst inst = new StoreInst(value, pointer);
        basicBlock.addInstruction(inst);
    }

    public static Value[] checkTypeConversion(BasicBlock basicBlock, Value left, Value right) {
        if (left.getType() == IntegerType.I32 && right.getType() == IntegerType.I8) {
            return new Value[]{left, buildConvInst(basicBlock, right)};
        } else if (left.getType() == IntegerType.I8 && right.getType() == IntegerType.I32) {
            return new Value[]{buildConvInst(basicBlock, left), right};
        } else if (left.getType() == IntegerType.I8 && right.getType() == IntegerType.I8) {
            return new Value[]{buildConvInst(basicBlock, left), buildConvInst(basicBlock, right)};
        } else {
            return new Value[]{left, right};
        }
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

}
