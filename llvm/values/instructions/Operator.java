package llvm.values.instructions;

public enum Operator {
    Add, Sub, Mul, Sdiv, Srem,
    Icmp, And, Or, Call, Alloca,
    Load, Store, GetElementPtr, Phi,
    Zext, Trunc, Br, Ret
}
