package llvm.values.instructions;

import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class RetInst extends Instruction {

    private Value value;

    public RetInst(Value value) {
        super(Operator.Ret);
        this.value = value;
        setType(value.getType());
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getOperator().toString().toLowerCase() + " ");
        IO.dealLLVMGeneration(super.getType() + " ");
        IO.dealLLVMGeneration(value.getName() + " ");
        IO.dealLLVMGeneration( "\n");
    }
}
