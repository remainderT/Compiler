package llvm.values.instructions;

import llvm.values.Instruction;

public class LoadInst extends Instruction {

    public LoadInst() {
        super(Operator.Load);
    }

}
