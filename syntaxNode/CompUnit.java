package syntaxNode;

import common.BasciNode;

import java.util.List;

public class CompUnit implements BasciNode {
    // CompUnit → {Decl} {FuncDef} MainFuncDef

    private List<Decl> decls;
    private List<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit(List<Decl> decls, List<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }

    @Override
    public void print() {

    }
}
