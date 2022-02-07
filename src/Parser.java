import java.util.ArrayList;

public class Parser {

    ArrayList<Token> tokens;

    public void parse(ArrayList<Token> t) {
        tokens = (ArrayList<Token>) t.clone();
    }

    void WinzigProcedure() {
        readToken(TokenType.Predefined_Keyword, "program");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, ":");
        ConstsProcedure();
        TypesProcedure();
        DclnsProcedure();
        SubProgsProcedure();
        BodyProcedure();
        NameProcedure();
        readToken(TokenType.Predefined_Operator, ".");
    }

    void ConstsProcedure() {
        if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "const") {
            readToken(TokenType.Predefined_Keyword, "const");
            ConstProcedure();
            while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ",") {
                readToken(TokenType.Predefined_Operator, ",");
                ConstProcedure();
            }
            readToken(TokenType.Predefined_Operator, ";");
        }
    }

    void ConstProcedure() {
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        ConstValueProcedure();
    }

    void ConstValueProcedure() {
        if (nextToken().type == TokenType.Integer) {
            readToken(TokenType.Integer);
        } else if (nextToken().type == TokenType.Char) {
            readToken(TokenType.Char);
        } else if (nextToken().type == TokenType.Identifier) {
            NameProcedure();
        }
    }

    void TypesProcedure() {
        if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "type") {
            readToken(TokenType.Identifier, "type");
            while (nextToken().type == TokenType.Identifier) {
                TypeProcedure();
                readToken(TokenType.Predefined_Operator, ";");
            }
        }
    }

    void TypeProcedure() {
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        LitListProcedure();
    }

    void LitListProcedure() {
        readToken(TokenType.Predefined_Operator, "(");
        NameProcedure();
        while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ",") {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
        }
        readToken(TokenType.Predefined_Operator, ")");
    }

    void SubProgsProcedure() {
        while (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "function") {
            FcnProcedure();
        }
    }

    void FcnProcedure() {
        readToken(TokenType.Predefined_Keyword, "function");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "(");
        ParamsProcedure();
        readToken(TokenType.Predefined_Operator, ")");
        readToken(TokenType.Predefined_Operator, ":");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, ";");
        ConstsProcedure();
        TypesProcedure();
        DclnsProcedure();
        BodyProcedure();
        NameProcedure();
        readToken(TokenType.Predefined_Operator, ";");
    }

    void ParamsProcedure() {
        DclnProcedure();
        while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ";") {
            readToken(TokenType.Predefined_Operator, ";");
            DclnProcedure();
        }
    }

    void DclnsProcedure() {
        if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "var") {
            readToken(TokenType.Predefined_Keyword, "var");
            while (nextToken().type == TokenType.Identifier) {
                DclnProcedure();
                readToken(TokenType.Predefined_Operator, ";");
            }
        }
    }

    void DclnProcedure() {
        NameProcedure();
        while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ",") {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
        }
        readToken(TokenType.Predefined_Operator, ":");
        NameProcedure();
    }

    void BodyProcedure() {
        readToken(TokenType.Predefined_Keyword, "begin");
        StatementProcedure();
        while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ";") {
            readToken(TokenType.Predefined_Operator, ";");
            StatementProcedure();
        }
        readToken(TokenType.Predefined_Keyword, "end");
    }

    void StatementProcedure() {
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {
            readToken(TokenType.Predefined_Keyword,"output");
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {

        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "output") {


        }
    }

    Token nextToken() {
        return tokens.get(0);
    }

    void readToken(TokenType t, String v) {
        if (nextToken().type == t && nextToken().value == v) {
            System.out.println("Consumed : " + tokens.get(0).visualize());
            tokens.remove(0);
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value + "\033[0m");
        }
    }

    void readToken(TokenType t) {
        if (nextToken().type == t) {
            System.out.println("Consumed : " + tokens.get(0).visualize());
            tokens.remove(0);
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value + "\033[0m");
        }
    }


}
