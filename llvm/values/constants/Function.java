package llvm.values.constants;

import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.types.VoidType;
import llvm.values.Arguement;
import llvm.values.BasicBlock;
import llvm.values.Constant;
import util.IO;

import java.util.ArrayList;
import java.util.List;

public class Function extends Constant {

    private boolean isLibrary;

    private List<BasicBlock> blocks;

    private List<Type> paramTypes;

    private List<Arguement> args;

    public Function(String name , Type type, boolean isLibrary) {
        super(name, type);
        this.isLibrary = isLibrary;
        this.blocks = new ArrayList<>();
        this.paramTypes = ((FunctionType) type).getParamTypes();
    }

    public void addBlock(BasicBlock block) {
        blocks.add(block);
    }

    public void print() {
        FunctionType ft = (FunctionType) this.getType();
        if (isLibrary) {
            IO.dealLLVMGeneration("declare dso_local ");
            IO.dealLLVMGeneration(ft.getReturnType() + " " + getName()  + "(");
            for (int i = 0; i < ft.getParamTypes().size(); i++) {
                IO.dealLLVMGeneration(ft.getParamTypes().get(i).toString());
                if (i != ft.getParamTypes().size() - 1) {
                    IO.dealLLVMGeneration(", ");
                }
            }
            IO.dealLLVMGeneration(")");
        } else {
            IO.dealLLVMGeneration("define dso_local ");
            IO.dealLLVMGeneration(ft.getReturnType() + " " + getName() + "(");
            for (int i = 0; i < ft.getParamTypes().size(); i++) {
                IO.dealLLVMGeneration(ft.getParamTypes().get(i).toString() + " %" + i);
                if (i != ft.getParamTypes().size() - 1) {
                    IO.dealLLVMGeneration(", ");
                }
            }
            IO.dealLLVMGeneration(")");
            IO.dealLLVMGeneration(" {\n");
            for (BasicBlock block : blocks) {
                block.print();
                IO.dealLLVMGeneration("\n");
            }
            if (getReturnType() == VoidType.Void && blocks.get(blocks.size() - 1).checkLastReturn()) {
                IO.dealLLVMGeneration("    ret void\n");
            }
            IO.dealLLVMGeneration("}");
        }
        IO.dealLLVMGeneration("\n\n");
    }

    public Type getReturnType() {
        return ((FunctionType) this.getType()).getReturnType();
    }

    @Override
    public String getName() {
        return "@" + super.getName();
    }

}
