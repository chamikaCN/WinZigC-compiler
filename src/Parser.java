import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Parser {

    ArrayList<Token> tokens;

    public void parse(ArrayList<Token> t) {
        tokens = (ArrayList<Token>) t.clone();
        WinzigProcedure();
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
        if (nextToken().value == "const") {
            readToken(TokenType.Predefined_Keyword, "const");
            ConstProcedure();
            while (nextToken().value == ",") {
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
        if (nextToken().value == "type") {
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
        while (nextToken().value == ",") {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
        }
        readToken(TokenType.Predefined_Operator, ")");
    }

    void SubProgsProcedure() {
        while (nextToken().value == "function") {
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
        while (nextToken().value == ";") {
            readToken(TokenType.Predefined_Operator, ";");
            DclnProcedure();
        }
    }

    void DclnsProcedure() {
        if (nextToken().value == "var") {
            readToken(TokenType.Predefined_Keyword, "var");
            while (nextToken().type == TokenType.Identifier) {
                DclnProcedure();
                readToken(TokenType.Predefined_Operator, ";");
            }
        }
    }

    void DclnProcedure() {
        NameProcedure();
        while (nextToken().value == ",") {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
        }
        readToken(TokenType.Predefined_Operator, ":");
        NameProcedure();
    }

    void BodyProcedure() {
        readToken(TokenType.Predefined_Keyword, "begin");
        StatementProcedure();
        while (nextToken().value == ";") {
            readToken(TokenType.Predefined_Operator, ";");
            StatementProcedure();
        }
        readToken(TokenType.Predefined_Keyword, "end");
    }

    void StatementProcedure() {
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
        } else if (nextToken().value == "output") {
            readToken(TokenType.Predefined_Keyword, "output");
            readToken(TokenType.Predefined_Operator, "(");
            while (nextToken().value == ",") {
                readToken(TokenType.Predefined_Operator, ",");
                OutExpProcedure();
            }
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().value == "if") {
            readToken(TokenType.Predefined_Keyword, "if");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "then");
            StatementProcedure();
            while (nextToken().value == "else") {
                readToken(TokenType.Predefined_Keyword, "else");
                StatementProcedure();
            }
        } else if (nextToken().value == "while") {
            readToken(TokenType.Predefined_Keyword, "while");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "do");
            StatementProcedure();
        } else if (nextToken().value == "repeat") {
            readToken(TokenType.Predefined_Keyword, "repeat");
            StatementProcedure();
            while (nextToken().value == ";") {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
            }
            readToken(TokenType.Predefined_Keyword, "until");
            ExpressionProcedure();
        } else if (nextToken().value == "for") {
            readToken(TokenType.Predefined_Keyword, "for");
            readToken(TokenType.Predefined_Operator, "(");
            ForStatProcedure();
            readToken(TokenType.Predefined_Operator, ";");
            ForExpProcedure();
            readToken(TokenType.Predefined_Operator, ";");
            ForStatProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            StatementProcedure();
        } else if (nextToken().value == "loop") {
            readToken(TokenType.Predefined_Keyword, "loop");
            StatementProcedure();
            while (nextToken().value == ";") {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
            }
            readToken(TokenType.Predefined_Keyword, "pool");
        } else if (nextToken().value == "case") {
            readToken(TokenType.Predefined_Keyword, "case");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "of");
            CaseclausesProcedure();
            OtherwiseClauseProcedure();
            readToken(TokenType.Predefined_Keyword, "end");
        } else if (nextToken().value == "read") {
            readToken(TokenType.Predefined_Keyword, "read");
            readToken(TokenType.Predefined_Operator, "(");
            NameProcedure();
            while (nextToken().value == ",") {
                readToken(TokenType.Predefined_Operator, ",");
                NameProcedure();
            }
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().value == "exit") {
            readToken(TokenType.Predefined_Keyword, "exit");
        } else if (nextToken().value == "return") {
            readToken(TokenType.Predefined_Keyword, "return");
            ExpressionProcedure();
        } else if (nextToken().value == "begin") {
            BodyProcedure();
        }
    }

    void OutExpProcedure() {
        if (nextToken().type == TokenType.String) {
            StringNodeProcedure();
        } else if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char || nextToken().type == TokenType.Integer ||
                new ArrayList<String>(Arrays.asList("+", "-", "(", "not", "eof", "succ", "pred", "chr", "ord")).contains(nextToken().value)) {
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
        while (nextToken().value == ",") {
            readToken(TokenType.Predefined_Operator, ",");
            CaseExpressionProcedure();
        }
        readToken(TokenType.Predefined_Operator, ":");
        StatementProcedure();
    }

    void CaseExpressionProcedure() {
        ConstValueProcedure();
        if (nextToken().value == "..") {
            readToken(TokenType.Predefined_Operator, "..");
            ConstValueProcedure();
        }
    }

    void OtherwiseClauseProcedure() {
        if (nextToken().value == "otherwise") {
            readToken(TokenType.Predefined_Keyword, "otherwise");
            StatementProcedure();
        } else {
            //TODO raise error
        }
    }

    void AssignmentProcedure() {
        NameProcedure();
        if (nextToken().value == ":=") {
            readToken(TokenType.Predefined_Operator, ":=");
            ExpressionProcedure();
        } else if (nextToken().value == ":=:") {
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
                new ArrayList<String>(Arrays.asList("+", "-", "(", "not", "eof", "succ", "pred", "chr", "ord")).contains(nextToken().value)) {
            ExpressionProcedure();
        }
    }

    void ExpressionProcedure() {
        TermProcedure();
        if (nextToken().value == "<=") {
            readToken(TokenType.Predefined_Operator, "<=");
            TermProcedure();
        } else if (nextToken().value == "<") {
            readToken(TokenType.Predefined_Operator, "<");
            TermProcedure();
        } else if (nextToken().value == ">=") {
            readToken(TokenType.Predefined_Operator, ">=");
            TermProcedure();
        } else if (nextToken().value == ">") {
            readToken(TokenType.Predefined_Operator, ">");
            TermProcedure();
        } else if (nextToken().value == "=") {
            readToken(TokenType.Predefined_Operator, "=");
            TermProcedure();
        } else if (nextToken().value == "<>") {
            readToken(TokenType.Predefined_Operator, "<>");
            TermProcedure();
        } else {
            //TODO raise error
        }
    }

    void TermProcedure() {
        FactorProcedure();
        while (new ArrayList<String>(Arrays.asList("+", "-", "or")).contains(nextToken().value)) {
            if (nextToken().value == "+") {
                readToken(TokenType.Predefined_Operator, "+");
                FactorProcedure();
            } else if (nextToken().value == "-") {
                readToken(TokenType.Predefined_Operator, "-");
                FactorProcedure();
            } else if (nextToken().value == "or") {
                readToken(TokenType.Predefined_Keyword, "or");
                FactorProcedure();
            }
        }
    }

    void FactorProcedure() {
        PrimaryProcedure();
        while (new ArrayList<String>(Arrays.asList("*", "/", "and", "mod")).contains(nextToken().value)) {
            if (nextToken().value == "*") {
                readToken(TokenType.Predefined_Operator, "*");
                PrimaryProcedure();
            } else if (nextToken().value == "/") {
                readToken(TokenType.Predefined_Operator, "/");
                PrimaryProcedure();
            } else if (nextToken().value == "and") {
                readToken(TokenType.Predefined_Keyword, "and");
                PrimaryProcedure();
            } else if (nextToken().value == "mod") {
                readToken(TokenType.Predefined_Keyword, "mod");
                PrimaryProcedure();
            }
        }
    }

    void PrimaryProcedure() {
        if (nextToken().value == "-") {
            readToken(TokenType.Predefined_Operator, "-");
            PrimaryProcedure();
        } else if (nextToken().value == "+") {
            readToken(TokenType.Predefined_Operator, "+");
            PrimaryProcedure();
        } else if (nextToken().value == "not") {
            readToken(TokenType.Predefined_Keyword, "not");
            PrimaryProcedure();
        } else if (nextToken().value == "eof") {
            readToken(TokenType.Predefined_Keyword, "eof");
        } else if (nextToken().type == TokenType.Identifier) {
            NameProcedure();
            if (nextToken().value == "(") {
                readToken(TokenType.Predefined_Operator, "(");
                ExpressionProcedure();
                while (nextToken().value == ",") {
                    readToken(TokenType.Predefined_Operator, ",");
                    ExpressionProcedure();
                }
                readToken(TokenType.Predefined_Operator, ")");
            }
        } else if (nextToken().type == TokenType.Integer) {
            readToken(TokenType.Integer);
        } else if (nextToken().type == TokenType.Char) {
            readToken(TokenType.Char);
        } else if (nextToken().value == "(") {
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().value == "succ") {
            readToken(TokenType.Predefined_Keyword, "succ");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().value == "pred") {
            readToken(TokenType.Predefined_Keyword, "pred");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().value == "chr") {
            readToken(TokenType.Predefined_Keyword, "chr");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (nextToken().value == "ord") {
            readToken(TokenType.Predefined_Keyword, "ord");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else {
            //TODO raie erreor
        }
    }

    void NameProcedure(){
        if(nextToken().type == TokenType.Identifier){
            readToken(TokenType.Identifier);
        } else{
            //TODO raise raise error
        }
    }

    Token nextToken() {
        if(tokens.size()>0) {
            return tokens.get(0);
        }else{
            //TODO raise wrong entry command
            return new Token(TokenType.Identifier,"");
        }
    }

    void readToken(TokenType t, String v) {
//        System.out.println(t.toString());
//        System.out.println(v);
//        System.out.println(nextToken().visualize());

        if (nextToken().type == t && Objects.equals(nextToken().value, v)) {
            System.out.println("Consumed : " + tokens.remove(0).visualize());
//            tokens.remove(0);
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at " + v.toUpperCase() + "\033[0m");
        }
    }

    void readToken(TokenType t) {
//        System.out.println(t.toString());
//        System.out.println(nextToken().visualize());

        if (nextToken().type == t) {
            System.out.println("Consumed : " + tokens.remove(0).visualize());
//            tokens.remove(0);
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at " + t.toString().toUpperCase() + "\033[0m");
        }
    }


}
