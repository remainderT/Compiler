package llvm.values.instructions;

import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import util.IO;

public class AllocaInst extends Instruction {

    private boolean isConst;

    private Type allocaType;

    public AllocaInst(BasicBlock basicBlock, boolean isConst, Type allocaType) {
        super(Operator.Alloca);
        super.setType(allocaType);
        this.setName("%" + basicBlock.getRegNumAndPlus());
        this.isConst = isConst;
        this.allocaType = allocaType;

    }

    public void print() {
        IO.dealLLVMGeneration("    " + super.getName() + " = alloca ");
        IO.dealLLVMGeneration(allocaType.toString());
        IO.dealLLVMGeneration("\n");
    }

}
