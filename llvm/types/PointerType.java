package llvm.types;

public class PointerType implements Type {

    private Type pointTo;

    public PointerType(Type pointTo) {
        this.pointTo = pointTo;
    }

    public Type getpointTo() {
        return pointTo;
    }

    @Override
    public String toString() {
        return pointTo.toString() + "*";
    }

}
