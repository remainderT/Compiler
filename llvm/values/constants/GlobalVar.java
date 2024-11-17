package llvm.values.constants;

import llvm.types.Type;
import llvm.values.Constant;
import llvm.values.Value;
import util.IO;

public class GlobalVar extends Constant {

    private boolean isConst;

    private Value value;

    public GlobalVar(String name, Type type, boolean isConst, Value value) {
        super(name, type);
        this.isConst = isConst;
        this.value = value;
        if (value.getType() != type) {    // 类型转换
            this.value.setType(type);
        }
    }

    public void print() {
        IO.dealLLVMGeneration(this.getName() +  " = dso_local global ");
        IO.dealLLVMGeneration(value.getType().toString() + " " + value.getName());
        IO.dealLLVMGeneration("\n\n");
    }

    public String getName() {
        return "@" + super.getName();
    }

}
