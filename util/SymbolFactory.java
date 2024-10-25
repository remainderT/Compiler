package util;

import common.SemanticType;
import symbol.Symbol;
import syntaxNode.BType;
import syntaxNode.FuncFParam;
import syntaxNode.FuncFParams;
import syntaxNode.FuncType;

import java.util.ArrayList;
import java.util.List;

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


    public static Symbol buildFunc(FuncType funcType, String name, FuncFParams funcFParams) {
        List<SemanticType> paramTypes = new ArrayList<>();
        if (funcFParams != null) {
            for (int i = 0; i < funcFParams.getFuncFParams().size(); i++) {
                FuncFParam param = funcFParams.getFuncFParams().get(i);
                if (param.getBType().getToken().getContent().equals("int")) {
                    if (param.getDimension() == 1) {
                        paramTypes.add(SemanticType.IntArray);
                    } else {
                        paramTypes.add(SemanticType.Int);
                    }
                } else {
                    if (param.getDimension() == 1) {
                        paramTypes.add(SemanticType.CharArray);
                    } else {
                        paramTypes.add(SemanticType.Char);
                    }
                }
            }
        }
        if (funcType.getToken().getContent().equals("int")) {
            return new Symbol(SemanticType.IntFunc, name, 0, paramTypes);
        } else if (funcType.getToken().getContent().equals("char")) {
            return new Symbol(SemanticType.CharFunc, name,1, paramTypes);
        } else {   // void
            return new Symbol(SemanticType.VoidFunc, name,2, paramTypes);
        }
    }

}
