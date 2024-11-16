package llvm.types;

import java.util.List;

public class FunctionType implements Type {

    private List<Type> parametersType;

    private Type returnType;

    public FunctionType(Type returnType, List<Type> parametersType) {
        this.returnType = returnType;
        this.parametersType = parametersType;
    }

    public List<Type> getParametersType() {
        return parametersType;
    }

    public Type getReturnType() {
        return returnType;
    }

}
