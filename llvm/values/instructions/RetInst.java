package llvm.values.instructions;

import llvm.types.VoidType;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class RetInst extends Instruction {

    private Value value;

    public RetInst(Value value) {
        super(Operator.Ret);
        this.value = value;
        if (value == null) {
            setType(new VoidType());
        } else {
            setType(value.getType());
        }
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getOperator().toString().toLowerCase() + " ");
        IO.dealLLVMGeneration(super.getType().toString());
        if (value != null) {
            IO.dealLLVMGeneration(" " + value.getName() + " ");
        }
        IO.dealLLVMGeneration( "\n");
    }
}
