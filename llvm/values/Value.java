package llvm.values;

import llvm.types.Type;

public class Value {

    private String name;

    private Type type;

    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

}
