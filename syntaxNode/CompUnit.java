package syntaxNode;

import common.BasciNode;
import common.SyntaxType;
import util.IO;

import java.util.List;

import static frontend.Parser.nodeMap;

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
        for (Decl decl : decls) {
            decl.print();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.print();
        }
        mainFuncDef.print();
        IO.dealSyntax(nodeMap.get(SyntaxType.CompUnit));
    }
}
