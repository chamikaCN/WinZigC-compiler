package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class winzigc {

    public static void main(String[] args) {
        
        // System.out.println(args[1]);
        // if (!Validator.validateArgs(args)) {
        //     return;
        // }
        String input = readFile(args[1]);
        LexScanner scanner = new LexScanner();
        ArrayList<Token> tokens = scanner.tokenize(input);
        Parser parser = new Parser();
        parser.parse(tokens);
        parser.visualizeTree();
    }

    private static String readFile(String path) {
        StringBuilder data = new StringBuilder();

        try {
            File f = new File(path);
            Scanner s = new Scanner(f);
            data.append(s.nextLine());
            while (s.hasNextLine()) {
                data.append('\n').append(s.nextLine());
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            e.printStackTrace();
        }
        return data.toString();
    }
}
