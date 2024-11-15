package llvm.values.constants;

import llvm.types.IntegerType;
import llvm.values.Constant;

public class IntConst extends Constant {

    private Integer value;

    public IntConst(int value) {
        super(Integer.toString(value), IntegerType.I32);
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void print() {
        System.out.print(value);
    }
}
