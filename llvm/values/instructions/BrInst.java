package llvm.values.instructions;

import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class BrInst extends Instruction {

    private BasicBlock fromBlock;

    private BasicBlock trueBlock;

    private BasicBlock falseBlock;

    private Value condition;

    public BrInst(BasicBlock fromBlock, BasicBlock trueBlock) {
        super(Operator.Br);
        this.fromBlock = fromBlock;
        this.trueBlock = trueBlock;
    }

    public BrInst(BasicBlock basicBlock, BasicBlock trueBlock, BasicBlock falseBlock, Value condition) {
        super(Operator.Br);
        this.fromBlock = basicBlock;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
        this.condition = condition;
    }

    public void print() {
        if (falseBlock == null) {
            IO.dealLLVMGeneration("    ");
            IO.dealLLVMGeneration("br label " + "%" + trueBlock.getLabelRegNum() + "\n");
        } else {
            IO.dealLLVMGeneration("    ");
            IO.dealLLVMGeneration("br i1");
            IO.dealLLVMGeneration(" " + condition.getName() + " ,label %" + trueBlock.getLabelRegNum() + " ,label %" + falseBlock.getLabelRegNum() + "\n");
        }
    }
}
