package util;

/*输入输出解析*/

import frontend.Token;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class IO {

    private static String ERROR_PATH;
    private static String LEXER_PATH;
    private static String TESTFILE_PATH;
    private static String PARSER_PATH;
    private static boolean isFirstWrite = true;

    public static void setErrorPath(String errorPath) {
        ERROR_PATH = errorPath;
    }

    public static void setLexerPath(String lexerPath) {
        LEXER_PATH = lexerPath;
    }

    public static void setTestfilePath(String testfilePath) {
        TESTFILE_PATH = testfilePath;
    }

    public static void setParserPath(String parserPath) {
        PARSER_PATH = parserPath;
    }

    public static String dealInput() {
        StringBuilder content = new StringBuilder();
        File file = new File(TESTFILE_PATH);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void dealOutput(String filepath, List<Token> tokens) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (Token token : tokens) {
                writer.write(token.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dealParseOut(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PARSER_PATH, !isFirstWrite))) {
            writer.write(content);
            isFirstWrite = false;  // 第一次写入后将标记设为 false
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dealError(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ERROR_PATH, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
