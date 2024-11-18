package llvm.values.instructions;

import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;
import util.ValueFactory;

public class BinaryInst extends Instruction {

    private Value left;

    private Value right;

    public BinaryInst(BasicBlock basicBlock, Operator operator, Value left, Value right) {
        super(operator);
        Value[] values = ValueFactory.checkTypeConversion(basicBlock, left, right);
        if (values[0] != left) {
            basicBlock.addInstruction((Instruction) values[0]);
        }
        if (values[1] != right) {
            basicBlock.addInstruction((Instruction) values[1]);
        }
        this.left = values[0];
        this.right = values[1];
        setName("%" + basicBlock.getRegNumAndPlus());
        setType(left.getType());
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getName() + " = " + super.getOperator().toString().toLowerCase() + " nsw ");
        IO.dealLLVMGeneration(left.getType() + " ");
        IO.dealLLVMGeneration(left.getName() + ", ");
        IO.dealLLVMGeneration(right.getName() + "\n");
    }

}
