package llvm.values.instructions;

import llvm.values.Instruction;
import llvm.values.Value;
import llvm.values.constants.IntConst;
import util.IO;

public class RetInst extends Instruction {

    private Value value;

    public RetInst(Value value) {
        super(Operator.Ret);
        this.value = value;
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getOperator().toString().toLowerCase() + " ");
        IO.dealLLVMGeneration(value.getType() + " ");
        IO.dealLLVMGeneration(((IntConst) value).getValue().toString() + " ");
        IO.dealLLVMGeneration( "\n");

    }
}
