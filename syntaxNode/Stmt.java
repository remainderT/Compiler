package syntaxNode;

import common.BasciNode;

public class Stmt implements BasciNode {
    /*  Stmt → LVal '=' Exp ';' // h i
        | [Exp] ';' // i
        | Block
        | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
        | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
        | 'break' ';' | 'continue' ';' // i m
        | 'return' [Exp] ';' // f i
        | LVal '=' 'getint''('')'';' // h i j
        | LVal '=' 'getchar''('')'';' // h i j
        | 'printf''('StringConst {','Exp}')'';' // i j l
*/
    @Override
    public void print() {

    }
}
