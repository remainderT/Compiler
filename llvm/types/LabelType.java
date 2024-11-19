package llvm.types;

public class LabelType implements Type {

    private int regNum;

    public LabelType(int regNum) {
        this.regNum = regNum;
    }

    @Override
    public String toString() {
        return ";<label>:" + regNum;
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }

    public int getRegNum() {
        return regNum;
    }

}
