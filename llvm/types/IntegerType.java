package llvm.types;

public class IntegerType implements Type {

    private final int bit;

    public static final IntegerType I1 = IntegerType.of(1);

    public static final IntegerType I8 = IntegerType.of(8);

    public static final IntegerType I32 = IntegerType.of(32);

    private IntegerType(int bit) {
        this.bit = bit;
    }

    public static IntegerType of(int bit) {
        return new IntegerType(bit);
    }

    @Override
    public String toString() {
        return "i" + bit;
    }

}
