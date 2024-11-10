package llvm.values;

import llvm.types.LabelType;
import util.IO;

import java.util.ArrayList;

public class BasicBlock extends Value {

    ArrayList<Instruction> instructions = new ArrayList<>();

    private int regNum = 0;

    public BasicBlock(String name, int regNum) {
        super(name, new LabelType(regNum++));
        this.regNum = regNum;
    }

    public void print() {
        IO.dealLLVMGeneration(super.getType().toString() + ":" + "\n");
        for (Instruction instruction : instructions) {
            instruction.print();
        }
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public int getRegNumAndPlus() {
        return regNum++;
    }

}
