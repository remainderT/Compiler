package llvm.values.constants;

import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.Constant;
import llvm.values.Value;
import util.IO;

public class GlobalVar extends Constant {

    private boolean isConst;

    private Value value;

    public GlobalVar(String name, Type type, boolean isConst, Value value) {
        super(name, new PointerType(type));
        this.isConst = isConst;
        this.value = value;
        if (value.getType() != type) {    // 类型转换
            this.value.setType(type);
        }
    }

    public String getName() {
        return "@" + super.getName();
    }

    public int getVal() {
        return ((IntConst) value).getValue();
    }

    public Type getAllocaType() {
        return value.getType();
    }

    @Override
    public void print() {
        if (isConst) {
            IO.dealLLVMGeneration("@" + super.getName() + " = dso_local constant ");
        } else {
            IO.dealLLVMGeneration("@" + super.getName() + " = dso_local global ");
        }
        IO.dealLLVMGeneration(value.getType().toString() + " " + value.getName());
        IO.dealLLVMGeneration("\n\n");
    }



}
