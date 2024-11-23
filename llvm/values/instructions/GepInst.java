package llvm.values.instructions;

import llvm.types.ArrayType;
import llvm.types.IntegerType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.BasicBlock;
import llvm.values.Instruction;
import llvm.values.Value;
import llvm.values.constants.GlobalArray;
import util.IO;
import util.ValueFactory;

public class GepInst extends Instruction {

    private Type arrayType;     // 指向的数组的类型

    private Type elementType;    // 数组元素类型

    private Value array;

    private Value offset;

    public GepInst(BasicBlock basicBlock, Value array, Value offset) {
        super(Operator.Gep);
        setName( "%" + basicBlock.getRegNumAndPlus());
        this.array = array;
        this.offset = offset;
        this.elementType = getElementType(array);
        this.arrayType = getArrayType(array);
        setType(new PointerType(elementType));
    }

    public Type getAllocaType() {
        return elementType;
    }

    public Type getElementType(Value array) {
        Type elementType = null;
        if (array instanceof AllocaInst) {
            elementType = ((AllocaInst) array).getElementType();
        } else if (array instanceof GlobalArray) {
            elementType = ((GlobalArray) array).getElememtType();
        } else if (array instanceof LoadInst) {
            elementType = ((PointerType) array.getType()).getPointTo();
        }
        return elementType;
    }

    public Type getArrayType(Value array) {
        Type arrayType = null;
        if (array instanceof AllocaInst) {
            arrayType = ((AllocaInst) array).getAllocaType();
        } else if (array instanceof GlobalArray) {
            arrayType = ((GlobalArray) array).getAllocType();
        } else if (array instanceof LoadInst) {   // a[]
            arrayType = elementType;
        }
        return arrayType;
    }

    public void print() {
        IO.dealLLVMGeneration("    " + getName() + " = getelementptr ");
        IO.dealLLVMGeneration(arrayType + ", " + new PointerType(arrayType)+ " " + array.getName() + ", ");
        if (arrayType != elementType) {  // a[]
            IO.dealLLVMGeneration("i32 0, ");
        }
        IO.dealLLVMGeneration("i32 " + offset.getName() + "\n");
    }

}
