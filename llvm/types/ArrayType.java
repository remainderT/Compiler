package llvm.types;

public class ArrayType implements Type {

    private Type elementType;

    private int capacity;

    public ArrayType(Type elementType, int capacity) {
        this.elementType = elementType;
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public Type getElementType() {
        return elementType;
    }

    @Override
    public String toString()  {
        return "[" + capacity + " x " + elementType.toString() + "]";

    }
}
