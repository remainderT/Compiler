package util;

/*输入输出解析*/

import common.Error;
import frontend.Semantic;
import frontend.Syntax;
import frontend.Token;
import llvm.IRModule;
import symbol.Symbol;
import symbol.SymbolTable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class IO {

    private static String STDIN_PATH;
    private static String STDOUT_PATH;
    private static String STDERR_PATH;
    private static int stage = 4;

    private static HashMap<Integer, String> map = new HashMap<>();

     static {
         setStderrPath("error.txt");
         setStdinPath("testfile.txt");
        map.put(1, "lexer.txt");  // lexical_analysis
        map.put(2, "parser.txt");   // syntax_analysis
        map.put(3, "symbol.txt");  // semantic_analysis
        map.put(4, "llvm_ir.txt");
         setStdoutPath(map.get(stage));
     }

    private static boolean isFirstWrite = true;

    public static void setStderrPath(String stderrPath) {
        STDERR_PATH = stderrPath;
    }

    public static void setStdoutPath(String stdoutPath) {
        STDOUT_PATH = stdoutPath;
    }

    public static void setStdinPath(String stdinPath) {
        STDIN_PATH = stdinPath;
    }

    public static String dealStdin() {
        StringBuilder content = new StringBuilder();
        File file = new File(STDIN_PATH);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void dealStdout(IRModule irModule, Semantic semantic, Syntax parser) {
        switch (stage) {
            case 1:
                dealLexical(parser.getTokens());
                break;
            case 2:
                parser.print();
                break;
            case 3:
                dealSemantic(semantic);
                break;
            case 4:
                irModule.print();
                break;
        }

    }

    public static void dealLexical(List<Token> tokens) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STDOUT_PATH))) {
            for (Token token : tokens) {
                writer.write(token.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dealSyntax(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STDOUT_PATH, !isFirstWrite))) {
            writer.write(content);
            isFirstWrite = false;  // 第一次写入后将标记设为 false
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dealSemantic(Semantic semantic) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STDOUT_PATH))) {
            for (SymbolTable symbolTable : semantic.getSymbolTables()) {
                for (Symbol symbol : symbolTable.getSymbolList()) {
                    writer.write(symbolTable.getIndex() + " " + symbol.toString() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dealLLVMGeneration(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STDOUT_PATH, !isFirstWrite))) {
            writer.write(content);
            isFirstWrite = false;  // 第一次写入后将标记设为 false
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void dealStderr(List<Error> errors) {
        Collections.sort(errors, new Comparator<Error>() {
            @Override
            public int compare(Error e1, Error e2) {
                return Integer.compare(e1.getLineNumber(), e2.getLineNumber());
            }
        });
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STDERR_PATH, false))) {
            for (Error error : errors) {
                writer.write(error.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
