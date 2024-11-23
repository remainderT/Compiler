package llvm.values.instructions;

import llvm.types.ArrayType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import util.IO;

public class AllocaInst extends Instruction {

    private Type allocaType;

    private boolean isConst;

    private int value;

    public AllocaInst(BasicBlock basicBlock, Type allocaType) {
        super(Operator.Alloca);
        super.setType(new PointerType(allocaType));
        setName("%" + basicBlock.getRegNumAndPlus());
        this.allocaType = allocaType;
    }

    public void setConst(boolean isConst) {
        this.isConst = isConst;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Type getAllocaType() {
        return allocaType;
    }

    public Type getElementType() {    // 对应数组类型
        return ((ArrayType) allocaType).getElementType();
    }

    @Override
    public void print() {
        IO.dealLLVMGeneration("    " + super.getName() + " = alloca " + allocaType.toString() + "\n");
    }

}
