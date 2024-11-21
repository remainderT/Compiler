package llvm.values.instructions;

import llvm.types.ArrayType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class GepInst extends Instruction {

    private Type pointType;

    private Value array;

    private Value offset;

    public GepInst(BasicBlock basicBlock, Value array, Value offset) {
        super(Operator.Gep);
        setName( "%" + basicBlock.getRegNumAndPlus());
        setType(offset.getType());
        pointType = new PointerType(array.getType());
        this.array = array;
        this.offset = offset;
    }

    public void print() {
        IO.dealLLVMGeneration("    " + getName() + " = getelementptr ");
        IO.dealLLVMGeneration(array.getType().toString() + ", " + pointType.toString() + " " + array.getName() + ", ");
        if (array.getType() instanceof ArrayType) {
            IO.dealLLVMGeneration("i32 0, ");
        }
        IO.dealLLVMGeneration("i32 " + offset.getName() + "\n");

    }

}
