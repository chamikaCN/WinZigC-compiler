import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Parser {

    ArrayList<Token> tokens;
    boolean debugProcedures = true;

    public void parse(ArrayList<Token> t) {
        tokens = (ArrayList<Token>) t.clone();
        WinzigProcedure();
    }

    void WinzigProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "WinZig" + "\033[0m");
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
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Consts" + "\033[0m");
        if (Objects.equals(nextToken().value, "const")) {
            readToken(TokenType.Predefined_Keyword, "const");
            ConstProcedure();
            while (Objects.equals(nextToken().value, ",")) {
                readToken(TokenType.Predefined_Operator, ",");
                ConstProcedure();
            }
            readToken(TokenType.Predefined_Operator, ";");
        }
    }

    void ConstProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Const" + "\033[0m");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        ConstValueProcedure();
    }

    void ConstValueProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ConstValue" + "\033[0m");
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
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Types" + "\033[0m");
        if (Objects.equals(nextToken().value, "type")) {
            readToken(TokenType.Identifier, "type");
            while (nextToken().type == TokenType.Identifier) {
                TypeProcedure();
                readToken(TokenType.Predefined_Operator, ";");
            }
        }
    }

    void TypeProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Type" + "\033[0m");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        LitListProcedure();
    }

    void LitListProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "LitList" + "\033[0m");
        readToken(TokenType.Predefined_Operator, "(");
        NameProcedure();
        while (Objects.equals(nextToken().value, ",")) {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
        }
        readToken(TokenType.Predefined_Operator, ")");
    }

    void SubProgsProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "SubProgs" + "\033[0m");
        while (Objects.equals(nextToken().value, "function")) {
            FcnProcedure();
        }
    }

    void FcnProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Fcn" + "\033[0m");
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
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Params" + "\033[0m");
        DclnProcedure();
        while (Objects.equals(nextToken().value, ";")) {
            readToken(TokenType.Predefined_Operator, ";");
            DclnProcedure();
        }
    }

    void DclnsProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Dclns" + "\033[0m");
        if (Objects.equals(nextToken().value, "var")) {
            readToken(TokenType.Predefined_Keyword, "var");
            while (nextToken().type == TokenType.Identifier) {
                DclnProcedure();
                readToken(TokenType.Predefined_Operator, ";");
            }
        }
    }

    void DclnProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Dcln" + "\033[0m");
        NameProcedure();
        while (Objects.equals(nextToken().value, ",")) {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
        }
        readToken(TokenType.Predefined_Operator, ":");
        NameProcedure();
    }

    void BodyProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Body" + "\033[0m");
        readToken(TokenType.Predefined_Keyword, "begin");
        StatementProcedure();
        while (Objects.equals(nextToken().value, ";")) {
            readToken(TokenType.Predefined_Operator, ";");
            StatementProcedure();
        }
        readToken(TokenType.Predefined_Keyword, "end");
    }

    void StatementProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Statement" + "\033[0m");
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
        } else if (Objects.equals(nextToken().value, "output")) {
            readToken(TokenType.Predefined_Keyword, "output");
            readToken(TokenType.Predefined_Operator, "(");
            OutExpProcedure();
            while (Objects.equals(nextToken().value, ",")) {
                readToken(TokenType.Predefined_Operator, ",");
                OutExpProcedure();
            }
            readToken(TokenType.Predefined_Operator, ")");
        } else if (Objects.equals(nextToken().value, "if")) {
            readToken(TokenType.Predefined_Keyword, "if");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "then");
            StatementProcedure();
            while (Objects.equals(nextToken().value, "else")) {
                readToken(TokenType.Predefined_Keyword, "else");
                StatementProcedure();
            }
        } else if (Objects.equals(nextToken().value, "while")) {
            readToken(TokenType.Predefined_Keyword, "while");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "do");
            StatementProcedure();
        } else if (Objects.equals(nextToken().value, "repeat")) {
            readToken(TokenType.Predefined_Keyword, "repeat");
            StatementProcedure();
            while (Objects.equals(nextToken().value, ";")) {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
            }
            readToken(TokenType.Predefined_Keyword, "until");
            ExpressionProcedure();
        } else if (Objects.equals(nextToken().value, "for")) {
            readToken(TokenType.Predefined_Keyword, "for");
            readToken(TokenType.Predefined_Operator, "(");
            ForStatProcedure();
            readToken(TokenType.Predefined_Operator, ";");
            ForExpProcedure();
            readToken(TokenType.Predefined_Operator, ";");
            ForStatProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            StatementProcedure();
        } else if (Objects.equals(nextToken().value, "loop")) {
            readToken(TokenType.Predefined_Keyword, "loop");
            StatementProcedure();
            while (Objects.equals(nextToken().value, ";")) {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
            }
            readToken(TokenType.Predefined_Keyword, "pool");
        } else if (Objects.equals(nextToken().value, "case")) {
            readToken(TokenType.Predefined_Keyword, "case");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "of");
            CaseclausesProcedure();
            OtherwiseClauseProcedure();
            readToken(TokenType.Predefined_Keyword, "end");
        } else if (Objects.equals(nextToken().value, "read")) {
            readToken(TokenType.Predefined_Keyword, "read");
            readToken(TokenType.Predefined_Operator, "(");
            NameProcedure();
            while (Objects.equals(nextToken().value, ",")) {
                readToken(TokenType.Predefined_Operator, ",");
                NameProcedure();
            }
            readToken(TokenType.Predefined_Operator, ")");
        } else if (Objects.equals(nextToken().value, "exit")) {
            readToken(TokenType.Predefined_Keyword, "exit");
        } else if (Objects.equals(nextToken().value, "return")) {
            readToken(TokenType.Predefined_Keyword, "return");
            ExpressionProcedure();
        } else if (Objects.equals(nextToken().value, "begin")) {
            BodyProcedure();
        }
    }

    void OutExpProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "OutExp" + "\033[0m");
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
        if (debugProcedures)
            System.out.println("\033[33;0m" + "StringNode" + "\033[0m");
        readToken(TokenType.String);
    }

    void CaseclausesProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Caseclauses" + "\033[0m");
        CaseClauseProcedure();
        readToken(TokenType.Predefined_Operator, ";");
        while (nextToken().type == TokenType.Integer || nextToken().type == TokenType.Char || nextToken().type == TokenType.Identifier) {
            CaseClauseProcedure();
            readToken(TokenType.Predefined_Operator, ";");
        }
    }

    void CaseClauseProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Caseclause" + "\033[0m");
        CaseExpressionProcedure();
        while (Objects.equals(nextToken().value, ",")) {
            readToken(TokenType.Predefined_Operator, ",");
            CaseExpressionProcedure();
        }
        readToken(TokenType.Predefined_Operator, ":");
        StatementProcedure();
    }

    void CaseExpressionProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "CaseExpression" + "\033[0m");
        ConstValueProcedure();
        if (Objects.equals(nextToken().value, "..")) {
            readToken(TokenType.Predefined_Operator, "..");
            ConstValueProcedure();
        }
    }

    void OtherwiseClauseProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Otherwise" + "\033[0m");
        if (Objects.equals(nextToken().value, "otherwise")) {
            readToken(TokenType.Predefined_Keyword, "otherwise");
            StatementProcedure();
        } else {
            //TODO raise error
        }
    }

    void AssignmentProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Assignment" + "\033[0m");
        NameProcedure();
        if (Objects.equals(nextToken().value, ":=")) {
            readToken(TokenType.Predefined_Operator, ":=");
            ExpressionProcedure();
        } else if (Objects.equals(nextToken().value, ":=:")) {
            readToken(TokenType.Predefined_Operator, ":=:");
            NameProcedure();
        } else {
            //TODO raise error
        }
    }

    void ForStatProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ForStat" + "\033[0m");
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
        }
    }

    void ForExpProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ForExp" + "\033[0m");
        if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char || nextToken().type == TokenType.Integer ||
                new ArrayList<String>(Arrays.asList("+", "-", "(", "not", "eof", "succ", "pred", "chr", "ord")).contains(nextToken().value)) {
            ExpressionProcedure();
        }
    }

    void ExpressionProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Expression" + "\033[0m");
        TermProcedure();
        if (Objects.equals(nextToken().value, "<=")) {
            readToken(TokenType.Predefined_Operator, "<=");
            TermProcedure();
        } else if (Objects.equals(nextToken().value, "<")) {
            readToken(TokenType.Predefined_Operator, "<");
            TermProcedure();
        } else if (Objects.equals(nextToken().value, ">=")) {
            readToken(TokenType.Predefined_Operator, ">=");
            TermProcedure();
        } else if (Objects.equals(nextToken().value, ">")) {
            readToken(TokenType.Predefined_Operator, ">");
            TermProcedure();
        } else if (Objects.equals(nextToken().value, "=")) {
            readToken(TokenType.Predefined_Operator, "=");
            TermProcedure();
        } else if (Objects.equals(nextToken().value, "<>")) {
            readToken(TokenType.Predefined_Operator, "<>");
            TermProcedure();
        } else {
            //TODO raise error
        }
    }

    void TermProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Term" + "\033[0m");
        FactorProcedure();
        while (new ArrayList<String>(Arrays.asList("+", "-", "or")).contains(nextToken().value)) {
            if (Objects.equals(nextToken().value, "+")) {
                readToken(TokenType.Predefined_Operator, "+");
                FactorProcedure();
            } else if (Objects.equals(nextToken().value, "-")) {
                readToken(TokenType.Predefined_Operator, "-");
                FactorProcedure();
            } else if (Objects.equals(nextToken().value, "or")) {
                readToken(TokenType.Predefined_Keyword, "or");
                FactorProcedure();
            }
        }
    }

    void FactorProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Factor" + "\033[0m");
        PrimaryProcedure();
        while (new ArrayList<String>(Arrays.asList("*", "/", "and", "mod")).contains(nextToken().value)) {
            if (Objects.equals(nextToken().value, "*")) {
                readToken(TokenType.Predefined_Operator, "*");
                PrimaryProcedure();
            } else if (Objects.equals(nextToken().value, "/")) {
                readToken(TokenType.Predefined_Operator, "/");
                PrimaryProcedure();
            } else if (Objects.equals(nextToken().value, "and")) {
                readToken(TokenType.Predefined_Keyword, "and");
                PrimaryProcedure();
            } else if (Objects.equals(nextToken().value, "mod")) {
                readToken(TokenType.Predefined_Keyword, "mod");
                PrimaryProcedure();
            }
        }
    }

    void PrimaryProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Primary" + "\033[0m");
        if (Objects.equals(nextToken().value, "-")) {
            readToken(TokenType.Predefined_Operator, "-");
            PrimaryProcedure();
        } else if (Objects.equals(nextToken().value, "+")) {
            readToken(TokenType.Predefined_Operator, "+");
            PrimaryProcedure();
        } else if (Objects.equals(nextToken().value, "not")) {
            readToken(TokenType.Predefined_Keyword, "not");
            PrimaryProcedure();
        } else if (Objects.equals(nextToken().value, "eof")) {
            readToken(TokenType.Predefined_Keyword, "eof");
        } else if (nextToken().type == TokenType.Identifier) {
            NameProcedure();
            if (Objects.equals(nextToken().value, "(")) {
                readToken(TokenType.Predefined_Operator, "(");
                ExpressionProcedure();
                while (Objects.equals(nextToken().value, ",")) {
                    readToken(TokenType.Predefined_Operator, ",");
                    ExpressionProcedure();
                }
                readToken(TokenType.Predefined_Operator, ")");
            }
        } else if (nextToken().type == TokenType.Integer) {
            readToken(TokenType.Integer);
        } else if (nextToken().type == TokenType.Char) {
            readToken(TokenType.Char);
        } else if (Objects.equals(nextToken().value, "(")) {
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (Objects.equals(nextToken().value, "succ")) {
            readToken(TokenType.Predefined_Keyword, "succ");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (Objects.equals(nextToken().value, "pred")) {
            readToken(TokenType.Predefined_Keyword, "pred");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (Objects.equals(nextToken().value, "chr")) {
            readToken(TokenType.Predefined_Keyword, "chr");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (Objects.equals(nextToken().value, "ord")) {
            readToken(TokenType.Predefined_Keyword, "ord");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else {
            //TODO raie erreor
        }
    }

    void NameProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Name" + "\033[0m");
        if (nextToken().type == TokenType.Identifier) {
            readToken(TokenType.Identifier);
        } else {
            //TODO raise raise error
        }
    }

    Token nextToken() {
        if (tokens.size() > 0) {
            return tokens.get(0);
        } else {
            //TODO raise wrong entry command
            return new Token(TokenType.Identifier, "");
        }
    }

    void readToken(TokenType t, String v) {
        if (nextToken().type == t && Objects.equals(nextToken().value, v)) {
            if (debugProcedures) {
                System.out.println("Consumed : " + tokens.remove(0).visualize());
            } else {
                tokens.remove(0);
            }
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at " + v.toUpperCase() + "\033[0m");
        }
    }

    void readToken(TokenType t) {
        if (nextToken().type == t) {
            if (debugProcedures) {
                System.out.println("Consumed : " + tokens.remove(0).visualize());
            } else {
                tokens.remove(0);
            }
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at " + t.toString().toUpperCase() + "\033[0m");
        }
    }


}
