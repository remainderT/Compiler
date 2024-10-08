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
    public static String dealInput(String filepath) {
        StringBuilder content = new StringBuilder();
        File file = new File(filepath);

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
                writer.newLine(); // 添加换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void dealError(String filepath, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {

        }
    }
}
