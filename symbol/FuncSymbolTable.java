package symbol;

import common.Token;

public class FuncSymbolTable extends SymbolTable {
    private Token returnType; // 0 int, 1 char, 2 void

    public FuncSymbolTable(int index, SymbolTable parent, Token returnType) {
        super(index, parent);
        this.returnType = returnType;
    }

    public int getReturnType() {
        if (returnType.getContent().equals("int")) {
            return 0;
        } else if (returnType.getContent().equals("char")) {
            return 1;
        } else {
            return 2;
        }
    }

}
