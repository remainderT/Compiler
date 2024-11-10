package llvm.values;

import llvm.types.Type;

public class Constant extends User {

    public Constant(String name, Type type) {
        super(name, type);
    }
}
