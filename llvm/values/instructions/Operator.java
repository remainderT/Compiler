package llvm.values.instructions;

public enum Operator {
    Add, Sub, Mul, Sdiv, Srem,
    Icmp, And, Or, Eq, Ne, Slt, Sle, Sgt, Sge,
    Call, Alloca, Load, Store, GetElementPtr,
    Zext, Trunc, Br, Ret
}
