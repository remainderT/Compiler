package llvm;

import llvm.values.Value;

import java.util.HashMap;

public class LLVMSymbolTable {

    int index; // 符号表编号

    private HashMap<String, Value> symbolTable;

    public LLVMSymbolTable(int index) {
        this.index = index;
        this.symbolTable = new HashMap<>();
    }

    public void put(String key, Value value) {
        symbolTable.put(key, value);
    }

    public Value get(String key) {
        return symbolTable.get(key);
    }

}
