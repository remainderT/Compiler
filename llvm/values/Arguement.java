package llvm.values;

import llvm.types.Type;

public class Arguement extends Value {

    private int index; // 从1开始

    public Arguement(Type type, int index) {
        super("%" + (index-1), type);
        this.index = index;
    }

}
