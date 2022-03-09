package main.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class LexScanner {

    private static String[] keyword_list = {"program", "var", "const", "type", "function", "return", "begin", "end", "output", "if", "then", "else", "while", "do", "case", "of", "otherwise", "repeat", "for", "until", "loop", "pool", "exit", "mod", "and", "or", "not", "read", "succ", "pred", "chr", "ord", "eof"};
    private static ArrayList<String> KEYWORDS = new ArrayList<>(Arrays.asList(keyword_list));
    private static String[] operator_list = {":=:", ":=", "..", "<=", "<>", "<", ">=", ">", "=", ":", ";", ".", ",", "(", ")", "+", "-", "*", "/"};
    private static ArrayList<String> OPERATORS = new ArrayList<>(Arrays.asList(operator_list));

    private ArrayList<Token> tokens = new ArrayList<>();

    public static ArrayList<Character> convertStringToCharList(String str) {
        ArrayList<Character> chars = new ArrayList<>();
        for (char ch : str.toCharArray()) {
            chars.add(ch);
        }
        return chars;
    }

    ArrayList<Token> tokenize(String s) {
        String currentString = s;
        while (currentString.length() > 0) {
            try {
                currentString = analyze(currentString);
            } catch (LexAnalyzerException e) {
                e.printStackTrace();
                currentString = currentString.substring(1);
            }
        }
        return screenTokens();
//        Iterator itr = tokens.iterator();
//        while (itr.hasNext()) {
//            System.out.println(((main.java.Token) (itr.next())).visualize());
//        }
    }

    private String analyze(String s) throws LexAnalyzerException {

        int charIndex = 0;
        int charLimit = s.length();
        char initial = s.charAt(0);

//        System.out.println("\033[32;0m" + charLimit + convertStringToCharList(s) + "\033[0m");

        if (initial == '{') {
            charIndex++;
            while (true) {
                if (charIndex >= charLimit) {
                    throw new LexAnalyzerException("Comment not closed");
                } else if (s.charAt(charIndex) == '}') {
                    tokens.add(new Token(TokenType.Comment, s.substring(1, charIndex)));
                    if (charIndex == charLimit - 1) {
                        s = "";
                    } else {
                        s = s.substring(charIndex + 1);
                    }
                    break;
                }
                charIndex++;
            }
        } else if (initial == '#') {
            charIndex++;
            while (true) {
                if (charIndex >= charLimit) {
                    tokens.add(new Token(TokenType.Comment, s.substring(1)));
                    s = "";
                    break;
                } else if (s.charAt(charIndex) == '\n') {
                    tokens.add(new Token(TokenType.Comment, s.substring(1, charIndex)));
                    s = s.substring(charIndex);
                    break;
                }
                charIndex++;
            }
        } else if (initial == '"') {
            charIndex++;
            while (true) {
                if (charIndex >= charLimit) {
                    throw new LexAnalyzerException("String not completed");
                } else if (s.charAt(charIndex) == '"') {
                    tokens.add(new Token(TokenType.String, s.substring(1, charIndex)));
                    if (charIndex == charLimit - 1) {
                        s = "";
                    } else {
                        s = s.substring(charIndex + 1);
                    }
                    break;
                }
                charIndex++;
            }
        } else if (initial == '\'') {
            if (s.length() >= 3 && s.charAt(2) == '\'') {
                tokens.add(new Token(TokenType.Char, s.substring(charIndex + 1, charIndex + 2)));
                s = s.substring(charIndex + 3);
            } else {
                throw new LexAnalyzerException("Character limit of 'char' exceeded");
            }
        } else if (Character.isWhitespace(initial)) {
            if (s.length() > 1) {
                tokens.add(new Token(TokenType.White_Space, s.substring(charIndex, charIndex + 1)));
                s = s.substring(charIndex + 1);
            } else if (s.length() == 1) {
                tokens.add(new Token(TokenType.White_Space, s));
                s = "";
            }

        } else if (Character.isLetterOrDigit(initial) || initial == '_') {
            charIndex++;
            String tokenString;
            while (true) {
                if (charIndex >= charLimit) {
                    tokenString = s;
                    s = "";
                    break;
                } else if (!Character.isLetterOrDigit(s.charAt(charIndex)) && !(s.charAt(charIndex) == '_')) {
                    tokenString = s.substring(0, charIndex);
                    s = s.substring(charIndex);
                    break;
                }
                charIndex++;
            }
            if (KEYWORDS.contains(tokenString)) {
                tokens.add(new Token(TokenType.Predefined_Keyword, tokenString));
            } else if (tokenString.charAt(0) == '_' || Character.isLetter(tokenString.charAt(0))) {
                tokens.add(new Token(TokenType.Identifier, tokenString));
            } else if (tokenString.matches("[0-9]+")) {
                tokens.add(new Token(TokenType.Integer, tokenString));
            } else {
                throw new LexAnalyzerException("Not an allowed identifier or a keyword");
            }
        } else if ((charLimit > 3) && Objects.equals(s.substring(0, 3), ":=:") && (Character.isLetterOrDigit(s.charAt(3)) || Character.isWhitespace(s.charAt(3)) || s.charAt(3) == '_' || OPERATORS.contains(s.substring(3, 4)))) {
            tokens.add(new Token(TokenType.Predefined_Operator, s.substring(0, 3)));
            s = s.substring(3);
        } else if ((charLimit == 3) && Objects.equals(s, ":=:")) {
            tokens.add(new Token(TokenType.Predefined_Operator, s));
            s = "";
        } else if ((charLimit > 2) && OPERATORS.contains(s.substring(0, 2)) && (Character.isLetterOrDigit(s.charAt(2)) || Character.isWhitespace(s.charAt(2)) || s.charAt(2) == '_' || OPERATORS.contains(s.substring(2, 3)))) {
            tokens.add(new Token(TokenType.Predefined_Operator, s.substring(0, 2)));
            s = s.substring(2);
        } else if ((charLimit == 2) && OPERATORS.contains(s)) {
            tokens.add(new Token(TokenType.Predefined_Operator, s));
            s = "";
        } else if ((charLimit > 1) && OPERATORS.contains(s.substring(0, 1)) && (Character.isLetterOrDigit(s.charAt(1)) || Character.isWhitespace(s.charAt(1)) || s.charAt(1) == '_' || OPERATORS.contains(s.substring(1, 2)))) {
            tokens.add(new Token(TokenType.Predefined_Operator, s.substring(0, 1)));
            s = s.substring(1);
        } else if ((charLimit == 1) && OPERATORS.contains(s)) {
            tokens.add(new Token(TokenType.Predefined_Operator, s));
            s = "";
        } else {
            throw new LexAnalyzerException("Illegal character found");
        }
        return s;
    }

    private ArrayList<Token> screenTokens() {
        ArrayList<Token> screened_tokens = new ArrayList<>();
        for (Token t : tokens) {
            if (t.type != TokenType.Comment && t.type != TokenType.White_Space) {
                screened_tokens.add(t);
            }
        }
        return screened_tokens;
    }
}
