public class Token {
    public final TokenType type;
    public final String value;

    public Token(TokenType t, String c) {
        type = t;
        value = c;
//        System.out.println("\033[33;0m" + "token: " + t.toString() + " : " + value + "\033[0m");
    }

    public String visualize() {
        if (type == TokenType.Identifier) {
            return "< id: " + value + " >";
        } else if (type == TokenType.Integer) {
            return "< int: " + value + " >";
        } else if (type == TokenType.Char) {
            return "< char: " + value + " >";
        } else if (type == TokenType.Predefined_Keyword) {
            return value.toUpperCase();
        } else if (type == TokenType.Predefined_Operator) {
            return "OP ::: "+value;
        }
        return type.toString();
    }
}