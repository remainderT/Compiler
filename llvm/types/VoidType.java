package llvm.types;

public class VoidType implements Type {

    public static final VoidType Void = new VoidType();

    @Override
    public String toString() {
        return "void";
    }

}
