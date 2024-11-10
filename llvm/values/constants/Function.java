package llvm.values.constants;

import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Constant;
import util.IO;

import java.util.ArrayList;
import java.util.List;

public class Function extends Constant {

    private String name;

    private boolean isLibrary;

    private List<BasicBlock> blocks;

    private int regNum = 0;

    public Function(String name , Type type, boolean isLibrary) {
        super(name, type);
        this.name = name;
        this.isLibrary = isLibrary;
        this.blocks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addBlock(BasicBlock block) {
        blocks.add(block);
    }

    public void print() {
        FunctionType ft = (FunctionType) this.getType();
        if (isLibrary) {
            IO.dealLLVMGeneration("declare dso_local ");
        } else {
            IO.dealLLVMGeneration("define dso_local ");
        }
        IO.dealLLVMGeneration(ft.getReturnType() + (" @") + this.getName() + "(");
        for (int i = 0; i < ft.getParametersType().size(); i++) {
            IO.dealLLVMGeneration(ft.getParametersType().get(i).toString());
            if (i != ft.getParametersType().size() - 1) {
                IO.dealLLVMGeneration(", ");
            }
        }
        IO.dealLLVMGeneration(")");
        if (!isLibrary) {
            IO.dealLLVMGeneration(" {\n");
            for (BasicBlock block : blocks) {
                block.print();
            }
            IO.dealLLVMGeneration("}");
        }
        IO.dealLLVMGeneration("\n\n");
    }

    public int getRegNum() {
        return regNum;
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }

}
