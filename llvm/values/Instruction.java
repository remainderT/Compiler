package llvm.values;

import llvm.values.instructions.Operator;

public class Instruction extends User {

    private Operator operator;

    public Instruction(Operator operator) {
        super("inst", null);
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    public void print() {

    }
}
