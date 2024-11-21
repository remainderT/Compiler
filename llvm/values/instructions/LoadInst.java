package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class LoadInst extends Instruction {

    private Value value;

    private Type  addr;

    public LoadInst(BasicBlock basicBlock, Value value) {
        super(Operator.Load);
        this.value = value;
        setName("%" + basicBlock.getRegNumAndPlus());
        if (value.getType() instanceof PointerType) {
            setType(((PointerType) value.getType()).getPointTo());
        } else {
            setType(value.getType());
        }
        addr = new PointerType(value.getType());
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getName() + " = load ");
        IO.dealLLVMGeneration(value.getType().toString());
        IO.dealLLVMGeneration(", " + addr.toString() + " " + value.getName());
        IO.dealLLVMGeneration("\n");
    }

}
