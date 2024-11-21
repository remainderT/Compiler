package llvm;

import llvm.values.Constant;
import llvm.values.constants.Function;

import java.util.ArrayList;
import java.util.List;

public class IRModule {

    private static final IRModule irModule = new IRModule();

    public static IRModule getInstance() {
        return irModule;
    }

    private List<Constant> globalValues;

    private List<Function> functions;

    public IRModule() {
        this.globalValues = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addGlobalValue(Constant globalValue) {
        globalValues.add(globalValue);
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void print() {
        for (int i = 0; i < functions.size() - 1; i++) {
            functions.get(i).print();
        }

        for (int i = 0; i < globalValues.size(); i++) {
            globalValues.get(i).print();
        }

        functions.get(functions.size() - 1).print();
    }

}
