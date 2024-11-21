package llvm.values.instructions;

public enum Operator {
    Add, Sub, Mul, Sdiv, Srem,
    And, Or, Eq, Ne, Slt, Sle, Sgt, Sge,
    Call, Alloca, Load, Store, Gep,
    Zext, Trunc, Br, Ret
}
