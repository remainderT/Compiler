package llvm.types;

import java.util.List;

public class FunctionType implements Type {

    private List<Type> paramTypes;

    private Type returnType;

    public FunctionType(Type returnType, List<Type> paramsType) {
        this.returnType = returnType;
        this.paramTypes = paramsType;
    }

    public List<Type> getParamTypes() {
        return paramTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

}
