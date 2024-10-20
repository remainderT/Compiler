package util;

import common.SemanticType;
import symbol.Symbol;
import syntaxNode.BType;
import syntaxNode.FuncFParams;
import syntaxNode.FuncType;

// 1代表数组，0代表变量
public class  SymbolFactory {

    public static Symbol buildConst0(BType bType, String name) {
        if (bType.getToken().getContent().equals("int")) {
            return new Symbol(SemanticType.ConstInt, name, 0, 1, 0);
        } else {
            return new Symbol(SemanticType.ConstChar, name, 0, 1, 1);
        }
    }

    public static Symbol buildConst1(BType bType, String name) {
        if (bType.getToken().getContent().equals("int")) {
            return new Symbol(SemanticType.ConstIntArray, name, 1, 1, 0);
        } else {
            return new Symbol(SemanticType.ConstCharArray, name,1, 1, 1);
        }
    }

    public static Symbol buildVar0(BType bType, String name) {
        if (bType.getToken().getContent().equals("int")) {
            return new Symbol(SemanticType.Int, name, 0, 0, 0);
        } else {
            return new Symbol(SemanticType.Char, name, 0, 0, 1);
        }
    }

    public static Symbol buildVar1(BType bType, String name) {
        if (bType.getToken().getContent().equals("int")) {
            return new Symbol(SemanticType.IntArray, name,1, 0, 0);
        } else {
            return new Symbol(SemanticType.CharArray, name,1, 0, 1);
        }
    }


    public static Symbol buildFunc(FuncType funcType, String name) {
        if (funcType.getToken().getContent().equals("int")) {
            return new Symbol(SemanticType.IntFunc, name,0 );
        } else if (funcType.getToken().getContent().equals("char")) {
            return new Symbol(SemanticType.CharFunc, name,1);
        } else {   // void
            return new Symbol(SemanticType.VoidFunc, name,2);
        }
    }

}
