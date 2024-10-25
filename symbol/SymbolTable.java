package symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTable {
    private int index; // 符号表编号

    private SymbolTable parent = null;  // 父符号表

    public HashMap<String, Symbol> symbols = new HashMap<>(); // 查找表

    public List<Symbol> symbolList = new ArrayList<>(); // 符号列表

    public SymbolTable(int index, SymbolTable parent) {
        this.parent = parent;
        this.index = index;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public List<Symbol> getSymbolList() {
        return symbolList;
    }

    public int getIndex() {
        return index;
    }

    public Symbol get(String ident) {
        Symbol symbol = symbols.get(ident);
        if (symbol != null) {
            return symbol;
        } else if (parent != null) {
            return parent.get(ident);
        }
        return null;
    }

    public boolean currentContains(String ident) {
        if (symbols.containsKey(ident)) {
            return true;
        }
        return false;
    }

    public boolean parentContains(String ident) {
        if (symbols.containsKey(ident)) {
            return true;
        } else if (parent != null) {
            return parent.parentContains(ident);
        }
        return false;
    }

    public void put(String ident, Symbol symbol) {
        symbolList.add(symbol);
        symbols.put(ident, symbol);
    }

}
