package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class winzigcExecution {

    static ArrayList<String[]> commands = new ArrayList<>();

    public static void main(String[] args){

        readFile("test/Test");
        for (String[] st:commands
             ) {
            System.out.println(Arrays.toString(st));
        }


    }

    private static void readFile(String path) {

        try {
            File f = new File(path);
            Scanner s = new Scanner(f);
            String line = s.nextLine();
            commands.add(line.trim().split("\\s+"));
            while (s.hasNextLine()) {
                line = s.nextLine();
                commands.add(line.trim().split("\\s+"));
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            e.printStackTrace();
        }
    }
}
