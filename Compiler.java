import frontend.Lexer;
import frontend.Token;
import util.IO;

import java.util.List;

public class Compiler {

    private static String ERROR_PATH = "error.txt";
    private static String LEXER_PATH = "lexer.txt";
    private static String TESTFILE_PATH = "testfile.txt";


    public static void main(String[] args) {
        String content = IO.dealInput(TESTFILE_PATH);
        List<Token> tokens = null;
        try {
            tokens = Lexer.analyze(content);
            IO.dealOutput(LEXER_PATH, tokens);
        } catch (Exception e) {
            IO.dealError(ERROR_PATH, e.getMessage());
        }
    }
}
