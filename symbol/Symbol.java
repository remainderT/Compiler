package symbol;

import common.SemanticType;

import java.util.List;

public class Symbol {

    private SemanticType type;    // 符号类型

    private String name; // 符号名

    private int dimension; // 0 var，1 array

    private int isConst = 0; // 0 变量，1 常量

    private int bType; // 0 int, 1 char

    private int FuncType; // 0 int, 1 char, 2 void

    private List<SemanticType> paramTypes; // 函数参数类型 0 int, 1 char

    public SemanticType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }

    public int getBType() {
        return bType;
    }

    public int getFuncType() {
        return FuncType;
    }

    public Boolean isConst() {
        return isConst == 1;
    }


    public Symbol(SemanticType type, String name, int dimension, int isConst, int bType) {
        this.type = type;
        this.name = name;
        this.dimension = dimension;
        this.isConst = isConst;
        this.bType = bType;
    }

    public Symbol(SemanticType type, String name, int funcType, List<SemanticType> paramTypes) {
        this.type = type;
        this.name = name;
        this.FuncType = funcType;
        this.paramTypes = paramTypes;
    }

    public String toString() {
        return name + " " + type.toString();
    }

    public List<SemanticType> getParamTypes() {
        return paramTypes;
    }
}
