package llvm;

import frontend.Token;
import llvm.values.Value;
import llvm.values.constants.IntConst;
import llvm.values.instructions.AllocaInst;
import llvm.values.instructions.CallInst;
import llvm.values.instructions.LoadInst;
import llvm.values.instructions.PhiInst;
import llvm.values.instructions.RetInst;
import llvm.values.instructions.TruncInst;
import llvm.values.instructions.ZextInst;

public class ValueFactory {





    public static IntConst getIntConst(Token token, Boolean isChar) {
        if (isChar) {
            return null;
        } else {
            return new IntConst(Integer.parseInt(token.getContent()));
        }
    }

    // instructions
    public static AllocaInst getAllocaInst() {
        return new AllocaInst();
    }

    public static CallInst getCallInst() {
        return new CallInst();
    }

    public static LoadInst getLoadInst() {
        return new LoadInst();
    }

    public static PhiInst getPhiInst() {
        return new PhiInst();
    }

    public static RetInst getRetInst(Value value) {
        return new RetInst(value);
    }

    public static TruncInst getTruncInst() {
        return new TruncInst();
    }

    public static ZextInst getZextInst() {
        return new ZextInst();
    }


}
