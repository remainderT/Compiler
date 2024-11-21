package llvm.values.instructions;

import llvm.types.FunctionType;
import llvm.types.VoidType;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import llvm.values.constants.Function;
import util.IO;

import java.util.ArrayList;
import java.util.List;

public class CallInst extends Instruction {

    private List<Value> params;

    private Function function;

    public CallInst(BasicBlock basicBlock, Function function, List<Value> params) {
        super(Operator.Call);
        if (function.getReturnType() instanceof VoidType) {
            setName("");
        } else {
            setName( "%" + basicBlock.getRegNumAndPlus());
        }
        FunctionType functionType = (FunctionType) function.getType();
        setType(functionType.getReturnType());
        this.function = function;
        this.params = params;
    }

    public void print() {
        IO.dealLLVMGeneration("    ");
        if (!getName().isEmpty()) {
            IO.dealLLVMGeneration(getName() + " = ");
        }
        IO.dealLLVMGeneration("call " + getType().toString() + " " + function.getName() + "(");
        for (int i = 0; i < params.size(); i++) {
            IO.dealLLVMGeneration(params.get(i).getType().toString() + " " + params.get(i).getName());
            if (i != params.size() - 1) {
                IO.dealLLVMGeneration(", ");
            }
        }
        IO.dealLLVMGeneration(")\n");
    }

}
