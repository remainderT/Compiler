package llvm.values.instructions;

import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class BinaryInst extends Instruction {

    private Value left;

    private Value right;

    public BinaryInst(BasicBlock basicBlock, Operator operator, Value left, Value right) {
        super(operator);
        this.left = left;
        this.right = right;
        setName("%" + basicBlock.getRegNumAndPlus());
        setType(left.getType());
    }

    public void print() {
        IO.dealLLVMGeneration("    " + super.getName() + " = " + super.getOperator().toString().toLowerCase() + " ");
        IO.dealLLVMGeneration(left.getType() + " ");
        IO.dealLLVMGeneration(left.getName() + ", ");
        IO.dealLLVMGeneration(right.getName() + "\n");
    }

}
