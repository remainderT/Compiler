package llvm;

import llvm.values.constants.Function;
import llvm.values.constants.GlobalVar;

import java.util.ArrayList;
import java.util.List;

public class IRModule {

    private static final IRModule irModule = new IRModule();

    public static IRModule getInstance() {
        return irModule;
    }

    private List<GlobalVar> globalVars;

    private List<Function> functions;

    public IRModule() {
        this.globalVars = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addGlobalVar(GlobalVar globalVar) {
        globalVars.add(globalVar);
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void print() {
        for (GlobalVar globalVar : globalVars) {
            globalVar.print();
        }
        for (Function function : functions) {
            function.print();
        }
    }
}
