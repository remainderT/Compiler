package llvm.values.constants;

import llvm.values.Constant;

public class GlobalVar extends Constant {

    private String name;

    public GlobalVar(String name) {
        super(name, null);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void print() {

    }

}
