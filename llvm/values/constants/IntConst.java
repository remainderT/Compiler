package llvm.values.constants;

import llvm.types.Type;
import llvm.values.Constant;

public class IntConst extends Constant {

    private int value;

    public IntConst(int value, Type type) {
        super(Integer.toString(value), type);
        this.value = value;
    }

    public void print() {
        System.out.print(value);
    }
}
