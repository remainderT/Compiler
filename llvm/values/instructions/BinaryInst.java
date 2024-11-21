package llvm.values.instructions;

import llvm.types.IntegerType;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import util.IO;
import util.ValueFactory;

public class BinaryInst extends Instruction {

    private Value left;

    private Value right;

    public BinaryInst(BasicBlock basicBlock, Operator operator, Value left, Value right) {
        super(operator);
        Value[] values = ValueFactory.checkTypeConversion(basicBlock, left, right);
        this.left = values[0];
        this.right = values[1];
        setName("%" + basicBlock.getRegNumAndPlus());
        setType(this.left.getType());
        if (isCond()) {
            setType(IntegerType.I1);
        }
    }

    public boolean isSlt() {
        return this.getOperator() == Operator.Slt;
    }

    public boolean isSle() {
        return this.getOperator() == Operator.Sle;
    }

    public boolean isSge() {
        return this.getOperator() == Operator.Sge;
    }

    public boolean isSgt() {
        return this.getOperator() == Operator.Sgt;
    }

    public boolean isEq() {
        return this.getOperator() == Operator.Eq;
    }

    public boolean isNe() {
        return this.getOperator() == Operator.Ne;
    }

    public boolean isCond() {
        return this.isSlt() || this.isSle() || this.isSge() || this.isSgt() || this.isEq() || this.isNe();
    }

    public String isNsw() {
        if (this.getOperator() == Operator.Add || this.getOperator() == Operator.Mul || this.getOperator() == Operator.Sub) {
            return "nsw ";
        }
        return "";
    }

    @Override
    public void print() {
        if (getOperator() == Operator.Add || getOperator() == Operator.Mul || getOperator() == Operator.Sub || getOperator() == Operator.Sdiv || getOperator() == Operator.Srem) {
            IO.dealLLVMGeneration("    " + super.getName() + " = " + super.getOperator().toString().toLowerCase() + " " +  isNsw());
        } else if (getOperator() == Operator.Eq || getOperator() == Operator.Ne || getOperator() == Operator.Slt || getOperator() == Operator.Sgt || getOperator() == Operator.Sle || getOperator() == Operator.Sge) {
            IO.dealLLVMGeneration("    " + super.getName() + " = icmp " + super.getOperator().toString().toLowerCase() + " ");
        }
        IO.dealLLVMGeneration(left.getType() + " ");
        IO.dealLLVMGeneration(left.getName() + ", ");
        IO.dealLLVMGeneration(right.getName() + "\n");
    }

}
