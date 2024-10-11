package frontend;

import common.TokenType;
import syntaxNode.*;
import syntaxNode.Number;

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

        while (preRead.getType() != TokenType.MAINTK && prePreRead.getType() != TokenType.LPARENT) {
            Decl decl = pDecl();
            decls.add(decl);
        }
        while (preRead.getType() != TokenType.MAINTK) {
            FuncDef funcDef = pFuncDef();
            funcDefs.add(funcDef);
        }
        mainFuncDef = pMainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
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
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        FuncType funcType;
        Token idenfr = null;
        Token lparent = null;
        FuncFParams funcFParams = null;
        Token rparent = null;
        Block block = null;

        funcType = pFuncType();
        if (now.getType() == TokenType.IDENFR) {
            idenfr = now;
            next();
        }
        if (now.getType() == TokenType.LPARENT) {
            lparent = now;
            next();
        }
        if (now.getType() != TokenType.RPARENT) {
            funcFParams = pFuncFParams();
        }
        if (now.getType() == TokenType.RPARENT) {
            rparent = now;
            next();
        }
        block = pBlock();
        return new FuncDef(funcType, idenfr, lparent, funcFParams, rparent, block);

    }

    private FuncFParams pFuncFParams() {
        //  FuncFParams → FuncFParam { ',' FuncFParam }
        List<FuncFParam> funcFParams = new ArrayList<>();
        List<Token> commas = new ArrayList<>();

        funcFParams.add(pFuncFParam());
        while (now.getType() == TokenType.COMMA) {
            commas.add(now);
            next();
            funcFParams.add(pFuncFParam());
        }
        return new FuncFParams(funcFParams, commas);
    }


    private FuncFParam pFuncFParam() {
        // FuncFParam → BType Ident ['[' ']']
        BType bType = null;
        Token idenfr = null;
        Token lbrack = null;
        Token rbrack = null;

        bType = pBType();
        if (now.getType() == TokenType.IDENFR) {
            idenfr = now;
            next();
        }
        if (now.getType() == TokenType.LBRACK) {
            lbrack = now;
            next();
            if (now.getType() == TokenType.RBRACK) {
                rbrack = now;
                next();
            }
        }
        return new FuncFParam(bType, idenfr, lbrack, rbrack);
    }

    private FuncType pFuncType() {
        //  FuncType → 'void' | 'int' | 'char'
        Token type = null;
        if (now.getType() == TokenType.VOIDTK || now.getType() == TokenType.INTTK || now.getType() == TokenType.CHARTK ) {
            type = now;
            next();
        }
        return new FuncType(type);
    }

    private MainFuncDef pMainFuncDef() {
        // MainFuncDef → 'int' 'main' '(' ')' Block
        Token inttk = null;
        Token maintk = null;
        Token lparent = null;
        Token rparent = null;
        Block block = null;

        if (now.getType() == TokenType.INTTK) {
            inttk = now;
            next();
        }
        if (now.getType() == TokenType.MAINTK) {
            maintk = now;
            next();
        }
        if (now.getType() == TokenType.LPARENT) {
            lparent = now;
            next();
        }
        if (now.getType() == TokenType.RPARENT) {
            rparent = now;
            next();
        }
        block = pBlock();
        return new MainFuncDef(inttk, maintk, lparent, rparent, block);
    }

    private ConstDecl pConstDecl() {
        // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        Token consttk = null;
        BType bType = null;
        List<ConstDef> constDefs = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        Token semicn = null;

        if (now.getType() == TokenType.CONSTTK) {
            consttk = now;
            next();
        }
        bType = pBType();
        constDefs.add(pConstDef());
        while (now.getType() == TokenType.COMMA) {
            commas.add(now);
            next();
            constDefs.add(pConstDef());
        }
        if (now.getType() == TokenType.SEMICN) {
            semicn = now;
            next();
        }
        return new ConstDecl(consttk, bType, constDefs, commas, semicn);
    }

    private VarDecl pVarDecl() {
        //  VarDecl → BType VarDef { ',' VarDef } ';'
        BType bType = null;
        List<VarDef> varDefs = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        Token semicn = null;

        bType = pBType();
        varDefs.add(pVarDef());
        while (now.getType() == TokenType.COMMA) {
            commas.add(now);
            next();
            varDefs.add(pVarDef());
        }
        if (now.getType() == TokenType.SEMICN) {
            semicn = now;
            next();
        }
        return new VarDecl(bType, varDefs, commas, semicn);
    }

    private BType pBType() {
        // BType → 'int' | 'char'
        BType bType = null;
        if (now.getType() == TokenType.INTCON || now.getType() == TokenType.CONSTTK) {
            bType = new BType(now);
            next();
        }
        return bType;
    }

    private ConstDef pConstDef() {
        //  ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
        Token idenfr = null;
        Token lbrack = null;
        ConstExp constExp = null;
        Token rbrack = null;
        Token assign = null;
        ConstInitVal constInitVal = null;

        if (now.getType() == TokenType.IDENFR) {
            idenfr = now;
            next();
        }
        if (now.getType() == TokenType.LBRACK) {
            lbrack = now;
            next();
        }
        constExp = pConstExp();
        if (now.getType() == TokenType.RBRACK) {
            rbrack = now;
            next();
        }
        if (now.getType() == TokenType.ASSIGN) {
            assign = now;
            next();
        }
        constInitVal = pConstInitVal();
        return new ConstDef(idenfr, lbrack, constExp, rbrack, assign, constInitVal);
    }

    private VarDef pVarDef() {
        //  Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
        Token idenfr = null;
        Token lbrack = null;
        ConstExp constExp = null;
        Token rbrack = null;
        Token assign = null;
        InitVal initVal = null;

        if (now.getType() == TokenType.IDENFR) {
            idenfr = now;
            next();
        }
        if (now.getType() == TokenType.LBRACK) {
            lbrack = now;
            next();
            constExp = pConstExp();
            if (now.getType() == TokenType.RBRACK) {
                rbrack = now;
                next();
            }
        }
        if (now.getType() == TokenType.ASSIGN) {
            assign = now;
            next();
            initVal = pInitVal();
        }
        return new VarDef(idenfr, lbrack, constExp, rbrack, assign, initVal);
    }

    private ConstExp pConstExp() {
        //  ConstExp → AddExp
        AddExp addExp = pAddExp();
        return new ConstExp(addExp);
    }

    private Exp pExp() {
        // Exp → AddExp
        AddExp addExp = pAddExp();
        return new Exp(addExp);
    }

    private AddExp pAddExp() {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp  修改为
        // AddExp -> MulExp { ('+' | '−') MulExp }
        List<MulExp> mulExps = new ArrayList<>();
        List<Token> operations = new ArrayList<>();
        mulExps.add(pMulExp());
        while (now.getType() == TokenType.PLUS || now.getType() == TokenType.MINU) {
            operations.add(now);
            next();
            mulExps.add(pMulExp());
        }
        return new AddExp(mulExps, operations);
    }

    private MulExp pMulExp() {
        //  MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp 修改为
        //  MulExp -> UnaryExp { ('*' | '/' | '%') UnaryExp }
        List<UnaryExp> unaryExps = new ArrayList<>();
        List<Token> operations = new ArrayList<>();
        unaryExps.add(pUnaryExp());
        while (now.getType() == TokenType.MULT || now.getType() == TokenType.DIV || now.getType() == TokenType.MOD) {
            operations.add(now);
            next();
            unaryExps.add(pUnaryExp());
        }
        return new MulExp(unaryExps, operations);
    }



    private UnaryExp pUnaryExp() {
        //  UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp  修改为

        return null;
    }

    private UnaryOp pUnaryOp() {
        // UnaryOp → '+' | '−' | '!'
        Token op = null;
        if (now.getType() == TokenType.PLUS || now.getType() == TokenType.MINU || now.getType() == TokenType.NOT) {
            op = now;
            next();
        }
        return new UnaryOp(op);
    }


    private ConstInitVal pConstInitVal() {
        //  ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
        return null;
    }

    private Block pBlock() {
        // Block → '{' { BlockItem } '}'
        Token lbrace = null;
        List<BlockItem> blockItems = new ArrayList<>();
        Token rbrace = null;

        if (now.getType() == TokenType.LBRACE) {
            lbrace = now;
            next();
        }
        while (now.getType() != TokenType.RBRACE) {
            blockItems.add(pBlockItem());
        }
        if (now.getType() == TokenType.RBRACE) {
            rbrace = now;
            next();
        }
        return new Block(lbrace, blockItems, rbrace);
    }

    private BlockItem pBlockItem() {
        // BlockItem -> Decl | Stmt
        Decl decl = null;
        Stmt stmt = null;

        if (now.getType() == TokenType.INTTK || now.getType() == TokenType.CONSTTK) {
            decl = pDecl();
        } else {
            stmt = pStmt();
        }
        return new BlockItem(decl, stmt);
    }

    private Stmt pStmt() {

        return null;
    }



    private InitVal pInitVal() {
        // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        return null;
    }

    private Number pNumber() {
        // Number → IntConst
        Token intcon = null;
        if (now.getType() == TokenType.INTCON) {
            intcon  = now;
            next();
        }
        return new Number(intcon);
    }

    private Cond PCond() {
        // Cond → LOrExp
        LOrExp lOrExp = pLOrExp();
        return new Cond(lOrExp);
    }

    private LOrExp pLOrExp() {
        //  LAndExp | LOrExp '||' LAndExp 修改为
        //  LOrExp -> LAndExp { '||' LAndExp }
        List<LAndExp> lAndExps = new ArrayList<>();
        List<Token> operations = new ArrayList<>();

        lAndExps.add(pLAndExp());
        while (now.getType() == TokenType.OR) {
            operations.add(now);
            next();
            lAndExps.add(pLAndExp());
        }
        return new LOrExp(lAndExps, operations);
    }

    private LAndExp pLAndExp() {
        // LAndExp → EqExp | LAndExp '&&' EqExp  修改为
        // LAndExp -> EqExp { '&&' EqExp }
        List<EqExp> eqExps = new ArrayList<>();
        List<Token> operations = new ArrayList<>();

        eqExps.add(pEqExp());
        while (now.getType() == TokenType.AND) {
            operations.add(now);
            next();
            eqExps.add(pEqExp());
        }
        return new LAndExp(eqExps, operations);
    }

    private EqExp pEqExp() {
        //  EqExp → RelExp | EqExp ('==' | '!=') RelExp 修改为
        // EqExp -> RelExp { ('==' | '!=') RelExp }
        List<RelExp> relExps = new ArrayList<>();
        List<Token> operations = new ArrayList<>();

        relExps.add(pRelExp());
        while (now.getType() == TokenType.NEQ || now.getType() == TokenType.EQL) {
            operations.add(now);
            next();
            relExps.add(pRelExp());
        }
        return new EqExp(relExps, operations);
    }

    private RelExp pRelExp() {
        // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp 修改为
        // RelExp -> AddExp { ('<' | '>' | '<=' | '>=') AddExp }
        List<AddExp> addExps = new ArrayList<>();
        List<Token> operations = new ArrayList<>();

        addExps.add(pAddExp());
        while (now.getType() == TokenType.LSS || now.getType() == TokenType.LEQ || now.getType() == TokenType.GRE || now.getType() == TokenType.GEQ) {
            operations.add(now);
            next();
            addExps.add(pAddExp());
        }
        return new RelExp(addExps, operations);
    }


    private ForStmt pForStmt() {
        //  ForStmt → LVal '=' Exp
        LVal lval = null;
        Token assign = null;
        Exp exp = null;

        lval = pLVal();
        if (now.getType() == TokenType.ASSIGN) {
            assign = now;
            next();
        }
        exp = pExp();
        return new ForStmt(lval, assign, exp);
    }

    private LVal pLVal() {
        // LVal → Ident ['[' Exp ']']
        Token idenfr = null;
        Token lbrack = null;
        Exp exp = null;
        Token rbrack = null;

        if (now.getType() == TokenType.IDENFR) {
            idenfr = now;
            next();
        }
        if (now.getType() == TokenType.LBRACK) {
            lbrack = now;
            next();
            exp = pExp();
            if (now.getType() == TokenType.RBRACK) {
                rbrack = now;
                next();
            }
        }
        return new LVal(idenfr, lbrack, exp, rbrack);
    }


    private PrimaryExp pPrimaryExp() {
        //  PrimaryExp → '(' Exp ')' | LVal | Number | Character

        return null;
    }

    private void next() {
        index++;
        now = tokens.get(index);
        preRead = tokens.get(index+1);
        prePreRead = tokens.get(index+2);
    }

}
