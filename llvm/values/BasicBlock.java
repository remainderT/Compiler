package llvm.values;

import llvm.types.LabelType;
import llvm.values.instructions.Operator;
import util.IO;

import java.util.ArrayList;

public class BasicBlock extends Value {

    ArrayList<Instruction> instructions = new ArrayList<>();

    private int regNum = 0;

    public BasicBlock(String name) {
        super(name, new LabelType(0));
        regNum = 1;
    }

    public void print() {
        IO.dealLLVMGeneration(super.getType().toString() + ":" + "\n");
        for (Instruction instruction : instructions) {
            instruction.print();
        }
    }

    public void addInstruction(Instruction instruction) {
        if (instructions.isEmpty() || !instructions.get(instructions.size() - 1).isTerminator()) {
            instructions.add(instruction);
        } else if (instruction.costReg()){
            regNum--;
        }
    }

    public int getRegNumAndPlus() {
        return regNum++;
    }

    public int getRegNum() {
        return regNum;
    }

    public void setLabelRegNum(int regNum) {
        ((LabelType) getType()).setRegNum(regNum);
        this.regNum = regNum + 1;
    }

    public int getLabelRegNum() {
        return ((LabelType) getType()).getRegNum();
    }

    public Boolean checkLastReturn() {
        if (instructions.isEmpty()) {
            return true;
        }
        Instruction lastInstruction = instructions.get(instructions.size() - 1);
        return lastInstruction.getOperator() != Operator.Ret;
    }

}
