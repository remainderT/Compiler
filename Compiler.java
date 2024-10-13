import frontend.Lexer;
import frontend.Token;
import util.IO;

import java.util.List;

public class Compiler {


    public static void main(String[] args) {
        IO.setErrorPath("error.txt");
        IO.setTestfilePath("testfile.txt");
        IO.setParserPath("parser.txt");
        String content = IO.dealInput();
        List<Token> tokens = null;
        try {
            tokens = Lexer.analyze(content);
        } catch (Exception e) {
            IO.dealError(e.getMessage());
        }
/*        Parser parser = new Parser(tokens);
        parser.analyze();
        parser.print();*/

    }
}
