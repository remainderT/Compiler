package frontend;

import common.StmtTpye;
import common.TokenType;
import syntaxNode.*;
import syntaxNode.Character;
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
        //  UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        PrimaryExp primaryExp = null;
        Token idenfr = null;
        Token lparent = null;
        FuncFParams funcFParams = null;
        Token rparent = null;
        UnaryOp unaryOp = null;
        UnaryExp unaryExp = null;

        if (now.getType() == TokenType.IDENFR && preRead.getType() == TokenType.LPARENT) {
            idenfr = now;
            next();
            lparent = now;
            funcFParams = pFuncFParams();
            if (now.getType() == TokenType.RPARENT) {
                rparent = now;
                next();
            }
            return new UnaryExp(idenfr, lparent, funcFParams, rparent);
        } else if (now.getType() == TokenType.PLUS || now.getType() == TokenType.MINU || now.getType() == TokenType.NOT) {
            unaryOp = pUnaryOp();
            unaryExp = pUnaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        } else {
            primaryExp = pPrimaryExp();
            return new UnaryExp(primaryExp);
        }
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
        List<ConstExp> constExps = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        Token lbrace = null;
        Token rbrace = null;
        Token stringConst;

        if (now.getType() == TokenType.STRCON) {
            stringConst = now;
            next();
            return new ConstInitVal(stringConst);
        } else if (now.getType() != TokenType.LBRACE) {
            constExps.add(pConstExp());
            return new ConstInitVal(constExps);
        } else if (now.getType() == TokenType.LBRACE) {
            lbrace = now;
            next();
            constExps.add(pConstExp());
            while (now.getType() == TokenType.COMMA) {
                commas.add(now);
                next();
                constExps.add(pConstExp());
            }
            if (now.getType() == TokenType.RBRACE) {
                rbrace = now;
                next();
            }
        }
        return new ConstInitVal(constExps, commas, lbrace, rbrace);
    }


    private InitVal pInitVal() {
        // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        List<Exp> expList = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        Token lbrace = null;
        Token rbrace = null;
        Token stringConst = null;

        if (now.getType() == TokenType.STRCON) {
            stringConst = now;
            next();
            return new InitVal(stringConst);
        } else if (now.getType() != TokenType.LBRACE) {
            expList.add(pExp());
            return new InitVal(expList);
        } else if (now.getType() == TokenType.LBRACE) {
            lbrace = now;
            next();
            expList.add(pExp());
            while (now.getType() == TokenType.COMMA) {
                commas.add(now);
                next();
                expList.add(pExp());
            }
            if (now.getType() == TokenType.RBRACE) {
                rbrace = now;
                next();
            }
        }
        return new InitVal(expList, commas, lbrace, rbrace);
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
/*    Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
            | [Exp] ';' //有无Exp两种情况
            | Block
            | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
            | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省，1种情况 2.
            ForStmt与Cond中缺省一个，3种情况 3. ForStmt与Cond中缺省两个，3种情况 4. ForStmt与Cond全部
            缺省，1种情况
            | 'break' ';' | 'continue' ';'
            | 'return' [Exp] ';' // 1.有Exp 2.无Exp
            | LVal '=' 'getint''('')'';'
            | LVal '=' 'getchar''('')'';'
            | 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp
*/
        LVal lval = null;
        List<Exp> exps = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        Block block = null;
        Token assign = null;
        Token iftk = null;
        Token fortk = null;
        Token breaktk = null;
        Token continuetk = null;
        Token returntk = null;
        Token getinttk = null;
        Token getchartk = null;
        Token printtk = null;
        Token lparent = null;
        Token rparent = null;
        Token strcon = null;
        Token elsetk = null;
        Token semicn = null;
        Token forSemicn1 = null;
        Token forSemicn2 = null;
        ForStmt forStmt1 = null;
        ForStmt forStmt2 = null;
        Stmt stmt = null;
        Stmt stmtElse = null;
        Cond cond = null;

        if (now.getType() == TokenType.LBRACE) {   // block
            block = pBlock();
            return new Stmt(StmtTpye.Block, block);
        } else if (now.getType() == TokenType.BREAKTK) {  // break;
            breaktk =  now;
            next();
            if (now.getType() == TokenType.SEMICN) {
                semicn =  now;
                next();
            }
            return new Stmt(StmtTpye.Break, breaktk, semicn);
        } else if (now.getType() == TokenType.CONTINUETK) {   // continue
            continuetk = now;
            next();
            if (now.getType() == TokenType.SEMICN) {
                semicn =  now;
                next();
            }
            return new Stmt(StmtTpye.Continue, continuetk, semicn);
        } else if (now.getType() == TokenType.RETURNTK) {  // return
            returntk =  now;
            next();
            if (now.getType() != TokenType.SEMICN) {
                exps.add(pExp());
            }
            if (now.getType() == TokenType.SEMICN) {
                semicn =  now;
            }
            return new Stmt(StmtTpye.Return, returntk, exps, semicn);
        } else if (now.getType() == TokenType.PRINTFTK) {  // print
            printtk = now;
            next();
            if (now.getType() == TokenType.LPARENT) {
                lparent = now;
                next();
                if (now.getType() == TokenType.STRCON) {
                    strcon = now;
                    next();
                    while (now.getType() == TokenType.COMMA) {
                        commas.add(now);
                        next();
                        exps.add(pExp());
                    }
                    if (now.getType() == TokenType.RPARENT) {
                        rparent = now;
                        next();
                        if (now.getType() == TokenType.SEMICN) {
                            semicn =  now;
                            next();
                        }
                    }
                }
            }
            return new Stmt(StmtTpye.Printf, printtk, lparent, exps, commas, rparent, semicn);
        } else if (now.getType() == TokenType.IFTK) {   // if
            iftk = now;
            next();
            if (now.getType() == TokenType.LPARENT) {
                lparent = now;
                next();
                cond = pCond();
                if (now.getType() == TokenType.RPARENT) {
                    rparent = now;
                    next();
                    stmt = pStmt();
                }
                if (now.getType() == TokenType.ELSETK) {
                    elsetk = now;
                    next();
                    stmtElse = pStmt();
                }
            }
            return new Stmt(StmtTpye.If, iftk, lparent, cond, rparent, stmt, elsetk, stmtElse);
        } else if (now.getType() == TokenType.FORTK) {  // for
            fortk = now;
            next();
            if (now.getType() == TokenType.LPARENT) {
                lparent = now;
                forStmt1 = pForStmt();
                if (now.getType() == TokenType.SEMICN) {
                    forSemicn1 = now;
                    next();
                    cond = pCond();
                    if (now.getType() == TokenType.SEMICN) {
                        forSemicn2 = now;
                        next();
                        forStmt2 = pForStmt();
                        if (now.getType() == TokenType.RPARENT) {
                            rparent = now;
                            next();
                            stmt = pStmt();
                        }
                    }
                }
            }
            return new Stmt(StmtTpye.For, fortk,lparent, forStmt1, forSemicn1, cond, forSemicn2, forStmt2, rparent, stmt);
        } else {
/*           LVal '=' Exp ';
                  | [Exp] ';'
                  | LVal '=' 'getint''('')'';'
                  | LVal '=' 'getchar''('')'';'

*/





        }


        return null;
    }

    private Boolean judge() {


        return Boolean.FALSE;
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

    private Character pCharacter() {
        Token chrcon = null;
        if (now.getType() == TokenType.CHRCON) {
            chrcon = now;
            next();
        }
        return new Character(chrcon);
    }

    private Cond pCond() {
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
        Token lparent = null;
        Exp exp = null;
        Token rparent = null;
        LVal lval = null;
        Number number = null;
        Character character = null;

        if (now.getType() == TokenType.LPARENT) {
            lparent = now;
            next();
            exp = pExp();
            if (now.getType() == TokenType.RPARENT) {
                rparent = now;
                next();
            }
            return new PrimaryExp(lparent, exp, rparent);
        } else if (now.getType() == TokenType.INTCON) {
            number = pNumber();
            return new PrimaryExp(number);
        } else if (now.getType() == TokenType.CONSTTK) {
            character = pCharacter();
            return new PrimaryExp(character);
        } else {
            lval = pLVal();
            return new PrimaryExp(lval);
        }
    }

    private void next() {
        index++;
        now = tokens.get(index);
        preRead = tokens.get(index+1);
        prePreRead = tokens.get(index+2);
    }

}
