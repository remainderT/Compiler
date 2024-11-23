package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class LoadInst extends Instruction {

    private Value addr;

    public LoadInst(BasicBlock basicBlock, Value addr) {
        super(Operator.Load);
        this.addr = addr;
        setName("%" + basicBlock.getRegNumAndPlus());
        setType(((PointerType) addr.getType()).getPointTo());
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getName() + " = load ");
        IO.dealLLVMGeneration(getType().toString() + ", " + addr.getType().toString() + " " + addr.getName());
        IO.dealLLVMGeneration("\n");
    }

}
