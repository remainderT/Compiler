package llvm.values.instructions;

import llvm.types.IntegerType;
import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;

public class ConvInst extends Instruction {

    private Value value;

    private Type from;

    private Type to;

    public ConvInst(BasicBlock basicBlock, Operator op, Value value){
        super(op);
        this.setName("%" + basicBlock.getRegNumAndPlus());
        Type type = op == Operator.Trunc ? IntegerType.I8 : IntegerType.I32;
        this.setType(type);
        this.from = value.getType();
        this.to = type;
        this.value = value;
    }

    public void print(){
        IO.dealLLVMGeneration("    " + super.getName() + " = " + super.getOperator().toString().toLowerCase() + " ");
        IO.dealLLVMGeneration(from.toString() + " " + value.getName() + " to " + to.toString());
        IO.dealLLVMGeneration("\n");
    }
}
