import java.util.ArrayList;
import java.util.Arrays;

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
        } else {
            //TODO raise error
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
            readToken(TokenType.Predefined_Keyword, "output");
            readToken(TokenType.Predefined_Operator, "(");
            while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ",") {
                readToken(TokenType.Predefined_Operator, ",");
                OutExpProcedure();
            }
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "if") {
            readToken(TokenType.Predefined_Keyword, "if");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "then");
            StatementProcedure();
            while (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "else") {
                readToken(TokenType.Predefined_Keyword, "else");
                StatementProcedure();
            }
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "while") {
            readToken(TokenType.Predefined_Keyword, "while");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "do");
            StatementProcedure();
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "repeat") {
            readToken(TokenType.Predefined_Keyword, "repeat");
            StatementProcedure();
            while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ";") {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
            }
            readToken(TokenType.Predefined_Keyword, "until");
            ExpressionProcedure();
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "for") {
            readToken(TokenType.Predefined_Keyword, "for");
            readToken(TokenType.Predefined_Operator, "(");
            ForStatProcedure();
            readToken(TokenType.Predefined_Operator, ";");
            ForExpProcedure();
            readToken(TokenType.Predefined_Operator, ";");
            ForStatProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            StatementProcedure();
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "loop") {
            readToken(TokenType.Predefined_Keyword, "loop");
            StatementProcedure();
            while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ";") {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
            }
            readToken(TokenType.Predefined_Keyword, "pool");
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "case") {
            readToken(TokenType.Predefined_Keyword, "case");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "of");
            CaseclausesProcedure();
            OtherwiseClauseProcedure();
            readToken(TokenType.Predefined_Keyword, "end");
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "read") {
            readToken(TokenType.Predefined_Keyword, "read");
            readToken(TokenType.Predefined_Operator, "(");
            NameProcedure();
            while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ",") {
                readToken(TokenType.Predefined_Operator, ",");
                NameProcedure();
            }
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "exit") {
            readToken(TokenType.Predefined_Keyword, "exit");
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "return") {
            readToken(TokenType.Predefined_Keyword, "return");
            ExpressionProcedure();
        } else if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "begin") {
            BodyProcedure();
        }
    }

    void OutExpProcedure() {
        if (nextToken().type == TokenType.String) {
            StringNodeProcedure();
        } else if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char || nextToken().type == TokenType.Integer ||
                (nextToken().type == TokenType.Predefined_Operator && new ArrayList<String>(Arrays.asList("+", "-", "(")).contains(nextToken().value)) ||
                (nextToken().type == TokenType.Predefined_Keyword && new ArrayList<String>(Arrays.asList("not", "eof", "succ", "pred", "chr", "ord")).contains(nextToken().value))) {
            ExpressionProcedure();
        } else {
            //TODO raise error
        }
    }

    void StringNodeProcedure() {
        readToken(TokenType.String);
    }

    void CaseclausesProcedure() {
        CaseClauseProcedure();
        readToken(TokenType.Predefined_Operator, ";");
        while (nextToken().type == TokenType.Integer || nextToken().type == TokenType.Char || nextToken().type == TokenType.Identifier) {
            CaseClauseProcedure();
            readToken(TokenType.Predefined_Operator, ";");
        }
    }

    void CaseClauseProcedure() {
        CaseExpressionProcedure();
        while (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ",") {
            readToken(TokenType.Predefined_Operator, ",");
            CaseExpressionProcedure();
        }
        readToken(TokenType.Predefined_Operator, ":");
        StatementProcedure();
    }

    void CaseExpressionProcedure() {
        ConstValueProcedure();
        if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == "..") {
            readToken(TokenType.Predefined_Operator, "..");
            ConstValueProcedure();
        }
    }

    void OtherwiseClauseProcedure() {
        if (nextToken().type == TokenType.Predefined_Keyword && nextToken().value == "otherwise") {
            readToken(TokenType.Predefined_Keyword, "otherwise");
            StatementProcedure();
        } else if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ";") {
            readToken(TokenType.Predefined_Operator, ";");
        } else {
            //TODO raise error
        }
    }

    void AssignmentProcedure() {
        NameProcedure();
        if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ":=") {
            readToken(TokenType.Predefined_Operator, ":=");
            ExpressionProcedure();
        } else if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ":=:") {
            readToken(TokenType.Predefined_Operator, ":=:");
            NameProcedure();
        } else {
            //TODO raise error
        }
    }

    void ForStatProcedure() {
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
        }
    }

    void ForExpProcedure() {
        if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char || nextToken().type == TokenType.Integer ||
                (nextToken().type == TokenType.Predefined_Operator && new ArrayList<String>(Arrays.asList("+", "-", "(")).contains(nextToken().value)) ||
                (nextToken().type == TokenType.Predefined_Keyword && new ArrayList<String>(Arrays.asList("not", "eof", "succ", "pred", "chr", "ord")).contains(nextToken().value))) {
            ExpressionProcedure();
        }
    }

    void ExpressionProcedure() {
        TermProcedure();
        if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == "<=") {
            readToken(TokenType.Predefined_Operator, "<=");
            TermProcedure();
        }else if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == "<") {
            readToken(TokenType.Predefined_Operator, "<");
            TermProcedure();
        }else if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ">=") {
            readToken(TokenType.Predefined_Operator, ">=");
            TermProcedure();
        }else if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == ">") {
            readToken(TokenType.Predefined_Operator, ">");
            TermProcedure();
        }else if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == "=") {
            readToken(TokenType.Predefined_Operator, "=");
            TermProcedure();
        }else if (nextToken().type == TokenType.Predefined_Operator && nextToken().value == "<>") {
            readToken(TokenType.Predefined_Operator, "<>");
            TermProcedure();
        }
    }

    void TermProcedure(){
        FactorProcedure();
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
