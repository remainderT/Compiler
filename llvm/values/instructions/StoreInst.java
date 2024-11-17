package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class StoreInst extends Instruction {

    private Value value;

    private Value pointer;     // allocaInst

    private Type addr;

    public StoreInst(Value value, Value pointer) {
        super(Operator.Store);
        this.value = value;
        this.pointer = pointer;
        this.addr = new PointerType(pointer.getType());
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    store ");
        IO.dealLLVMGeneration(value.getType().toString());
        IO.dealLLVMGeneration(" " + value.getName() + ", ");
        IO.dealLLVMGeneration(addr.toString());
        IO.dealLLVMGeneration(" " + pointer.getName());
        IO.dealLLVMGeneration("\n");
    }

}
