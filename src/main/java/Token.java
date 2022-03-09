package main.java;

class Token {
    final TokenType type;
    final String value;

    Token(TokenType t, String c) {
        type = t;
        value = c;
    }

    String visualize() {
        if (type == TokenType.Identifier) {
            return "< id: " + value + " >";
        } else if (type == TokenType.Integer) {
            return "< int: " + value + " >";
        } else if (type == TokenType.Char) {
            return "< char: " + value + " >";
        } else if (type == TokenType.Predefined_Keyword) {
            return value.toUpperCase();
        } else if (type == TokenType.Predefined_Operator) {
            return "OP ::: " + value;
        }
        return type.toString();
    }
}