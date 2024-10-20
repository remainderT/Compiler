package symbol;

import common.SemanticType;

public class Symbol {

    private SemanticType type;    // 符号类型

    private String name; // 符号名

    private int dimension; // 0 var，1 array

    private int isConst;// 0 变量，1 常量

    private int bType; // 0 int, 1 char

    private int FuncType; // 0 int, 1 char, 2 void


    public SemanticType getType() {
        return type;
    }


    public Symbol(SemanticType type, String name, int dimension, int isConst, int bType) {
        this.type = type;
        this.name = name;
        this.dimension = dimension;
        this.isConst = isConst;
        this.bType = bType;
    }

    public Symbol(SemanticType type, String name, int funcType) {
        this.type = type;
        this.name = name;
        this.FuncType = funcType;
    }

    public String toString() {
        return name + " " + type.toString();
    }

}
