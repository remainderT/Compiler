package llvm.values.constants;

import llvm.types.ArrayType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.Constant;
import llvm.values.Value;
import util.IO;

import java.util.ArrayList;
import java.util.List;

public class GlobalArray extends Constant {

    private boolean isConst;

    private int capacity;

    private Type AllocType;

    private Type elememtType;

    private List<Value> array;

    public GlobalArray(String name, Boolean isConst, int capacity, Type elememtType) {
        super(name, null);
        ArrayType arrayType = new ArrayType(elememtType, capacity);
        setType(new PointerType(arrayType));
        this.AllocType = arrayType;
        this.isConst = isConst;
        this.capacity = capacity;
        this.elememtType = elememtType;
        this.array = new ArrayList<>();
    }

    private boolean allZero() {
        for (int i = 0; i < capacity; i++) {
            if (array.get(i) instanceof IntConst && ((IntConst) array.get(i)).getValue() != 0) {
                return false;
            }
        }
        return false;
    }

    public Type getElememtType() {
        return elememtType;
    }

    public Type getAllocType() {
        return AllocType;
    }

    public String getName() {
        return "@" + super.getName();
    }

    public void addElement(Value value) {
        array.add(value);
    }

    @Override
    public void print() {
        if (isConst) {
            IO.dealLLVMGeneration("@" + super.getName() + " = dso_local constant ");
        } else {
            IO.dealLLVMGeneration("@" + super.getName() + " = dso_local global ");
        }
        IO.dealLLVMGeneration(this.AllocType + " ");
        if (allZero()) {
            IO.dealLLVMGeneration("zeroinitializer");
        }
        else {
            for (int i = 0; i < capacity; i++) {
                if (i == 0) {
                    IO.dealLLVMGeneration(" [ ");
                }
                IO.dealLLVMGeneration(elememtType + " " + array.get(i).getName());
                if (i != capacity - 1) {
                    IO.dealLLVMGeneration(", ");
                } else {
                    IO.dealLLVMGeneration(" ]");
                }
            }
        }
        IO.dealLLVMGeneration("\n\n");
    }

}
