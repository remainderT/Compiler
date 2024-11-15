package llvm.values.instructions;

import llvm.values.Instruction;

public class CallInst extends Instruction {

    public CallInst() {
        super(Operator.Call);
    }

    public String getString() {
        return "%" + this.getName();
    }
}
