package llvm.values.instructions;

import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import util.IO;

public class AllocaInst extends Instruction {

    private Type type;

    public AllocaInst(BasicBlock basicBlock, Type allocaType) {
        super(Operator.Alloca);
        super.setType(allocaType);
        setName("%" + basicBlock.getRegNumAndPlus());
        this.type = allocaType;

    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getName() + " = alloca " + type.toString() + "\n");
    }

}
