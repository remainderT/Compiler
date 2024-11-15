package llvm.values;

import llvm.types.LabelType;
import util.IO;

import java.util.ArrayList;

public class BasicBlock extends Value {

    ArrayList<Instruction> instructions = new ArrayList<>();

    public BasicBlock(String name, LabelType type) {
        super(name, type);
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

}
