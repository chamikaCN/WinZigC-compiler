import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class winzigc {

    static ArrayList<Token> tokens;

    public static void main(String[] args) {
        String input = readFile("test/winzig_15");
//        System.out.println(input);
        LexScanner scanner = new LexScanner();
        tokens = scanner.tokenize(input);
//        Iterator itr = tokens.iterator();
//        while (itr.hasNext()) {
//            System.out.println(((Token) (itr.next())).visualize());
//        }

        Parser parser = new Parser();
        parser.parse(tokens);
    }

    public static String readFile(String path) {
        StringBuilder data = new StringBuilder();

        try {
            File f = new File(path);
            Scanner s = new Scanner(f);
            data.append(s.nextLine());
            while (s.hasNextLine()) {
                data.append('\n' + s.nextLine());
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            e.printStackTrace();
        }
        return data.toString();
    }
}
