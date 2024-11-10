package llvm.values.constants;

import llvm.types.IntegerType;
import llvm.values.Constant;

public class IntConst extends Constant {

    private int value;

    public IntConst(int value) {
        super(Integer.toString(value), IntegerType.I32);
        this.value = value;
    }

    public void print() {
        System.out.print(value);
    }
}
