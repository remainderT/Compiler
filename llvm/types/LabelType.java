package llvm.types;

public class LabelType implements Type {

    private int blockNum;

    public LabelType(int blockNum) {
        this.blockNum = blockNum;
    }

    @Override
    public String toString() {
        return "label_" + blockNum;
    }
}
