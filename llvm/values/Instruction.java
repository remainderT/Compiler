package llvm.values;

import llvm.values.instructions.Operator;

public class Instruction extends Value {

    private Operator operator;

    public Instruction(Operator operator) {
        super("", null);
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    public void print() {

    }
}
