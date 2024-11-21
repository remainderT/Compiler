package llvm.values.instructions;

import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class StoreInst extends Instruction {

    private Value value;

    private Value addr;     // allocaInst

    private Type addrType;

    public StoreInst(Value value, Value addr) {
        super(Operator.Store);
        this.value = value;
        this.addr = addr;
        this.addrType = new PointerType(addr.getType());
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    store ");
        IO.dealLLVMGeneration(value.getType().toString());
        IO.dealLLVMGeneration(" " + value.getName() + ", ");
        IO.dealLLVMGeneration(addrType.toString());
        IO.dealLLVMGeneration(" " + addr.getName());
        IO.dealLLVMGeneration("\n");
    }

}
