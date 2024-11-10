package llvm.values.instructions;

import llvm.values.Instruction;

public class AllocaInst extends Instruction {

    public AllocaInst() {
        super(Operator.Alloca);
    }

}
