import frontend.Lexer;
import frontend.Parser;
import frontend.Token;
import common.Error;
import util.IO;

import java.util.ArrayList;
import java.util.List;

public class Compiler {

    public static void main(String[] args) {
        String content = IO.dealStdin();
        List<Error> errors = new ArrayList<>();
        Lexer lexer = new Lexer(content, errors);
        List<Token> tokens = lexer.analyze();
        Parser parser = new Parser(tokens, errors);
        parser.analyze();
        if (errors.size() == 0) {
            IO.dealStdout(parser);
        } else{
            IO.dealStderr(errors);
        }
    }
}
