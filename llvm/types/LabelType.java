package llvm.types;

public class LabelType implements Type {

    private int regNum;

    public LabelType(int regNum) {
        this.regNum = regNum;
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }

    public int getRegNum() {
        return regNum;
    }

    @Override
    public String toString() {
        return ";<label>:" + regNum;
    }

}
