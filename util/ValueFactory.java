package util;

import llvm.types.IntegerType;
import llvm.types.Type;
import llvm.values.Arguement;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import llvm.values.constants.Function;
import llvm.values.constants.GlobalVar;
import llvm.values.constants.IntConst;
import llvm.values.instructions.AllocaInst;
import llvm.values.instructions.BinaryInst;
import llvm.values.instructions.CallInst;
import llvm.values.instructions.ConvInst;
import llvm.values.instructions.LoadInst;
import llvm.values.instructions.Operator;
import llvm.values.instructions.RetInst;
import llvm.values.instructions.StoreInst;

import java.util.List;

public class ValueFactory {

    public static IntConst getIntConst(String number, Boolean isChar) {
        if (isChar) {
            return new IntConst(number.charAt(0), IntegerType.I8);
        } else {
            return new IntConst(Integer.parseInt(number), IntegerType.I32);
        }
    }

    public static GlobalVar getGlobalVar(String name, Type type, boolean isConst, Value value) {
        return new GlobalVar(name, type, isConst, value);
    }

    public static Arguement getArguement(Type type, int index) {
        return new Arguement(type, index);
    }

    // instructions
    public static CallInst getCallInst(BasicBlock basicBlock, Function function, List<Value> params) {
        return new CallInst(basicBlock, function, params);
    }

    public static LoadInst getLoadInst(BasicBlock basicBlock, Value value) {
        return new LoadInst(basicBlock, value);
    }

    public static RetInst getRetInst(Value value) {
        return new RetInst(value);
    }

    public static ConvInst getConvInst(BasicBlock basicBlock, Value value) {
        Type from = value.getType();
        Operator op = from == IntegerType.I32 ? Operator.Trunc : Operator.Zext;
        return new ConvInst(basicBlock, op,  value);
    }

    public static BinaryInst getBinaryInst(BasicBlock basicBlock, Operator op, Value left, Value right) {
        return new BinaryInst(basicBlock, op, left, right);
    }

    public static AllocaInst getAllocaInst(BasicBlock basicBlock, boolean isConst, Type allocaType) {
        return new AllocaInst(basicBlock, isConst, allocaType);
    }

    public static StoreInst getStoreInst(Value value, Value pointer) {
        return new StoreInst(value, pointer);
    }

    public static Value[] checkTypeConversion(BasicBlock basicBlock, Value left, Value right) {
        if (left.getType() == IntegerType.I32 && right.getType() == IntegerType.I8) {
            return new Value[]{left, getConvInst(basicBlock, right)};
        } else if (left.getType() == IntegerType.I8 && right.getType() == IntegerType.I32) {
            return new Value[]{getConvInst(basicBlock, left), right};
        } else if (left.getType() == IntegerType.I8 && right.getType() == IntegerType.I8) {
            return new Value[]{getConvInst(basicBlock, left), getConvInst(basicBlock, right)};
        } else {
            return new Value[]{left, right};
        }
    }

}
