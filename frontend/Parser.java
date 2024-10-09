package frontend;

import common.TokenType;
import syntaxNode.BType;
import syntaxNode.CompUnit;
import syntaxNode.ConstDecl;
import syntaxNode.ConstDef;
import syntaxNode.Decl;
import syntaxNode.FuncDef;
import syntaxNode.MainFuncDef;
import syntaxNode.VarDecl;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private CompUnit compUnit;
    int index = 0;
    private Token now;
    private Token preRead;
    private Token prePreRead;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        now = tokens.get(0);
        preRead = tokens.get(1);
        prePreRead = tokens.get(2);
    }

    public void analyze() {
        this.compUnit = pCompUnit();
    }

    private CompUnit pCompUnit() {
        // CompUnit -> {Decl} {FuncDef} MainFuncDef
        List<Decl> decls = new ArrayList<>();
        List<FuncDef> funcDefs = new ArrayList<>();
        MainFuncDef mainFuncDef = null;
        CompUnit compUnit = null;
        while (preRead.getType() != TokenType.MAINTK && prePreRead.getType() != TokenType.LPARENT) {
            Decl decl = pDecl();
            decls.add(decl);
        }
        while (preRead.getType() != TokenType.MAINTK) {
            FuncDef funcDef = pFuncDef();
            funcDefs.add(funcDef);
        }
        mainFuncDef = pMainFuncDef();
        compUnit = new CompUnit(decls, funcDefs, mainFuncDef);
        return compUnit;
    }

    private Decl pDecl() {
        // Decl → ConstDecl | VarDecl
        ConstDecl constDecl = null;
        VarDecl varDecl = null;
        Decl decl = null;
        if (now.getType() == TokenType.CONSTTK) {
            constDecl = pConstDecl();
            decl = new Decl(constDecl);
        } else {
            varDecl = pVarDecl();
            decl = new Decl(varDecl);
        }
        return decl;
    }

    private FuncDef pFuncDef() {

        return null;
    }


    private MainFuncDef pMainFuncDef() {

        return null;
    }

    private ConstDecl pConstDecl() {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        return null;
    }

    private VarDecl pVarDecl() {
        return null;
    }

    private BType pBType() {
        // BType → 'int' | 'char'
        if (now.getType() == TokenType.INTCON) {

        } else if (now.getType() == TokenType.CONSTTK) {

        }
        return null;
    }

    private ConstDef pConstDef() {
        return null;
    }

    private void next() {
        index++;
        now = tokens.get(index);
        preRead = tokens.get(index+1);
        prePreRead = tokens.get(index+2);
    }

}
