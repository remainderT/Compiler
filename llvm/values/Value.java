package llvm.values;

import llvm.IRModule;
import llvm.types.Type;

public class Value {

    private final IRModule irModule = IRModule.getInstance();

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

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public IRModule getModule() {
        return irModule;
    }

}
