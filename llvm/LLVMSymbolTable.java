package llvm;

import llvm.values.Value;

import java.util.HashMap;

public class LLVMSymbolTable {

    private int index; // 符号表编号

    private HashMap<String, Value> symbolTable;

    private LLVMSymbolTable father;

    public LLVMSymbolTable(int index, LLVMSymbolTable father) {
        this.index = index;
        this.father = father;
        this.symbolTable = new HashMap<>();
    }

    public void put(String key, Value value) {
        symbolTable.put(key, value);
    }

    public Value get(String key) {
        return symbolTable.get(key);
    }

    public LLVMSymbolTable getFather() {
        return father;
    }

}
