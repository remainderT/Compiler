import common.Error;
import frontend.Lexer;
import frontend.Semantic;
import frontend.Syntax;
import frontend.Token;
import llvm.IRModule;
import llvm.LLVMGenerator;
import util.IO;

import java.util.ArrayList;
import java.util.List;

public class Compiler {

    public static void main(String[] args) {
        String content = IO.dealStdin();
        List<Error> errors = new ArrayList<>();
        Lexer lexer = new Lexer(content, errors);
        List<Token> tokens = lexer.analyze();
        Syntax syntax = new Syntax(tokens, errors);
        syntax.analyze();
        Semantic semantic = new Semantic(syntax.getCompUnit(), errors);
        semantic.fCompUnit();
        LLVMGenerator generator = new LLVMGenerator(semantic.getCompUnit());
        generator.Generate();
        IRModule module = IRModule.getInstance();
        if (errors.isEmpty()) {
            IO.dealStdout(module, semantic, syntax);
        } else{
            IO.dealStderr(errors);
        }
    }
}
