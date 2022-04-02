package main.java;

import java.io.*;
import java.util.*;

class Parser {

    private ArrayList<Token> tokens;
    private boolean debugProcedures = true;
    private Stack<Node> treeNodes = new Stack<>();
    private Set<String> grammarRules = new HashSet<>();

    void parse(ArrayList<Token> t) {
        tokens = (ArrayList<Token>) t.clone();
        try {
            WinzigProcedure();
            System.out.println("rules :"+ grammarRules.size());
            for (String s: grammarRules) {
                System.out.println(s);
            }
        } catch (ParserException e) {
            System.out.println(nextToken().type + " : " + nextToken().value);
            e.printStackTrace();
        }
    }

    private void WinzigProcedure() throws ParserException {
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
        grammarRules.add("Winzig -> 'program' Name ':' Consts Types Dclns SubProgs Body Name '.'");
        buildTree("program", 7);

    }

    private void ConstsProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Consts" + "\033[0m");
        if (Objects.equals(nextToken().value, "const")) {
            readToken(TokenType.Predefined_Keyword, "const");
            ConstProcedure();
            int n = 1;
            while (Objects.equals(nextToken().value, ",")) {
                readToken(TokenType.Predefined_Operator, ",");
                ConstProcedure();
                n++;
            }
            readToken(TokenType.Predefined_Operator, ";");
            grammarRules.add("Consts -> 'const' Const list ',' ';'");
            buildTree("consts", n);

        } else {
            grammarRules.add("Consts -> ");
            buildTree("consts", 0);
        }
    }

    private void ConstProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Const" + "\033[0m");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        ConstValueProcedure();
        grammarRules.add("Const -> Name '=' ConstValue");
        buildTree("const", 2);
    }

    private void ConstValueProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ConstValue" + "\033[0m");
        if (nextToken().type == TokenType.Integer) {
            readToken(TokenType.Integer);
//            grammarRules.add("ConstValue -> '<integer>'");
        } else if (nextToken().type == TokenType.Char) {
            readToken(TokenType.Char);
//            grammarRules.add("ConstValue -> '<char>'");
        } else if (nextToken().type == TokenType.Identifier) {
            NameProcedure();
//            grammarRules.add("ConstValue -> Name");
        } else {
            throw new ParserException("");
        }
    }

    private void TypesProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Types" + "\033[0m");
        if (Objects.equals(nextToken().value, "type")) {
            readToken(TokenType.Predefined_Keyword, "type");
            TypeProcedure();
            int n = 1;
            readToken(TokenType.Predefined_Operator, ";");
            while (nextToken().type == TokenType.Identifier) {
                TypeProcedure();
                n++;
                readToken(TokenType.Predefined_Operator, ";");
            }
            grammarRules.add("Types -> 'type' (Type ';')+");
            buildTree("types", n);
        } else {
            grammarRules.add("Types -> ");
            buildTree("types", 0);
        }
    }

    private void TypeProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Type" + "\033[0m");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        LitListProcedure();
        grammarRules.add("Type -> Name '=' LitList");
        buildTree("type", 2);
    }

    private void LitListProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "LitList" + "\033[0m");
        readToken(TokenType.Predefined_Operator, "(");
        NameProcedure();
        int n = 1;
        while (Objects.equals(nextToken().value, ",")) {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
            n++;
        }
        readToken(TokenType.Predefined_Operator, ")");
        grammarRules.add("LitList -> '(' Name list ',' ')'");
        buildTree("lit", n);
    }

    private void SubProgsProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "SubProgs" + "\033[0m");
        int n = 0;
        while (Objects.equals(nextToken().value, "function")) {
            FcnProcedure();
            n++;
        }
        grammarRules.add("SubProgs -> Fcn*");
        buildTree("subprogs", n);
    }

    private void FcnProcedure() throws ParserException {
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
        grammarRules.add("Fcn -> 'function' Name '(' Params ')' ':' Name ';' Consts Types Dclns Body Name ';'");
        buildTree("fcn", 8);
    }

    private void ParamsProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Params" + "\033[0m");
        DclnProcedure();
        int n = 1;
        while (Objects.equals(nextToken().value, ";")) {
            readToken(TokenType.Predefined_Operator, ";");
            DclnProcedure();
            n++;
        }
        grammarRules.add("Params -> Dcln list ';'");
        buildTree("params", n);
    }

    private void DclnsProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Dclns" + "\033[0m");
        if (Objects.equals(nextToken().value, "var")) {
            readToken(TokenType.Predefined_Keyword, "var");
            DclnProcedure();
            int n = 1;
            readToken(TokenType.Predefined_Operator, ";");
            while (nextToken().type == TokenType.Identifier) {
                DclnProcedure();
                n++;
                readToken(TokenType.Predefined_Operator, ";");
            }
            grammarRules.add("Dclns -> 'var' (Dcln ';')+");
            buildTree("dclns", n);
        } else {
            grammarRules.add("Dclns -> ");
            buildTree("dclns", 0);
        }
    }

    private void DclnProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Dcln" + "\033[0m");
        NameProcedure();
        int n = 1;
        while (Objects.equals(nextToken().value, ",")) {
            readToken(TokenType.Predefined_Operator, ",");
            NameProcedure();
            n++;
        }
        readToken(TokenType.Predefined_Operator, ":");
        NameProcedure();
        grammarRules.add("Dcln -> Name list ',' ':' Name");
        buildTree("var", n + 1);
    }

    private void BodyProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Body" + "\033[0m");
        readToken(TokenType.Predefined_Keyword, "begin");
        StatementProcedure();
        int n = 1;
        while (Objects.equals(nextToken().value, ";")) {
            readToken(TokenType.Predefined_Operator, ";");
            if (!Objects.equals(nextToken().value, "end")) {
                StatementProcedure();
                n++;
            } else {
                buildTree("<null>", 0);
                n++;
            }
        }
        readToken(TokenType.Predefined_Keyword, "end");
        grammarRules.add("Body -> 'begin' Statement list ';' 'end'");
        buildTree("block", n);
    }

    private void StatementProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Statement" + "\033[0m");
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
//            grammarRules.add("Statement -> Assignment");
        } else if (Objects.equals(nextToken().value, "output")) {
            readToken(TokenType.Predefined_Keyword, "output");
            readToken(TokenType.Predefined_Operator, "(");
            OutExpProcedure();
            int n = 1;
            while (Objects.equals(nextToken().value, ",")) {
                readToken(TokenType.Predefined_Operator, ",");
                OutExpProcedure();
                n++;
            }
            readToken(TokenType.Predefined_Operator, ")");
            grammarRules.add("Statement -> 'output' '(' OutExp list ',' ')'");
            buildTree("output", n);
        } else if (Objects.equals(nextToken().value, "if")) {
            readToken(TokenType.Predefined_Keyword, "if");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "then");
            StatementProcedure();
            int n = 2;
            if (Objects.equals(nextToken().value, "else")) {
                readToken(TokenType.Predefined_Keyword, "else");
                StatementProcedure();
                n++;
            }
            grammarRules.add("Statement -> 'if' Expression 'then' Statement ('else' Statement)?");
            buildTree("if", n);
        } else if (Objects.equals(nextToken().value, "while")) {
            readToken(TokenType.Predefined_Keyword, "while");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "do");
            StatementProcedure();
            grammarRules.add("Statement -> 'while' Expression 'do' Statement");
            buildTree("while", 2);
        } else if (Objects.equals(nextToken().value, "repeat")) {
            readToken(TokenType.Predefined_Keyword, "repeat");
            StatementProcedure();
            int n = 1;
            while (Objects.equals(nextToken().value, ";")) {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
                n++;
            }
            readToken(TokenType.Predefined_Keyword, "until");
            ExpressionProcedure();
            grammarRules.add("Statement -> 'repeat' Statement list ';' 'until' Expression");
            buildTree("repeat", n + 1);
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
            grammarRules.add("Statement -> 'for' '(' ForStat ';' ForExp ';' ForStat ')' Statement");
            buildTree("for", 4);
        } else if (Objects.equals(nextToken().value, "loop")) {
            readToken(TokenType.Predefined_Keyword, "loop");
            StatementProcedure();
            int n = 1;
            while (Objects.equals(nextToken().value, ";")) {
                readToken(TokenType.Predefined_Operator, ";");
                StatementProcedure();
                n++;
            }
            readToken(TokenType.Predefined_Keyword, "pool");
            grammarRules.add("Statement -> 'loop' Statement list ';' 'pool'");
            buildTree("loop", n);
        } else if (Objects.equals(nextToken().value, "case")) {
            readToken(TokenType.Predefined_Keyword, "case");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "of");
            int caseCount = CaseclausesProcedure();
            int otherwise = OtherwiseClauseProcedure();
            readToken(TokenType.Predefined_Keyword, "end");
            grammarRules.add("Statement -> 'case' Expression 'of' Caseclauses OtherwiseClause 'end'");
            buildTree("case", 1 + caseCount + otherwise);
        } else if (Objects.equals(nextToken().value, "read")) {
            readToken(TokenType.Predefined_Keyword, "read");
            readToken(TokenType.Predefined_Operator, "(");
            NameProcedure();
            int n = 1;
            while (Objects.equals(nextToken().value, ",")) {
                readToken(TokenType.Predefined_Operator, ",");
                NameProcedure();
                n++;
            }
            readToken(TokenType.Predefined_Operator, ")");
            grammarRules.add("Statement -> 'read' '(' Name list ',' ')'");
            buildTree("read", n);
        } else if (Objects.equals(nextToken().value, "exit")) {
            readToken(TokenType.Predefined_Keyword, "exit");
            grammarRules.add("Statement -> 'exit'");
            buildTree("exit", 0);
        } else if (Objects.equals(nextToken().value, "return")) {
            readToken(TokenType.Predefined_Keyword, "return");
            ExpressionProcedure();
            grammarRules.add("Statement -> 'return' Expression");
            buildTree("return", 1);
        } else if (Objects.equals(nextToken().value, "begin")) {
            BodyProcedure();
//            grammarRules.add("Statement -> Body");
        } else {
            grammarRules.add("Statement -> ");
            buildTree("<null>", 0);
        }
    }

    private void OutExpProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "OutExp" + "\033[0m");
        if (nextToken().type == TokenType.String) {
            StringNodeProcedure();
            grammarRules.add("OutExp -> StringNode");
            buildTree("string", 1);
        } else if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char
                || nextToken().type == TokenType.Integer ||
                new ArrayList<>(Arrays.asList("+", "-", "(", "not", "eof", "succ", "pred", "chr", "ord"))
                        .contains(nextToken().value)) {
            ExpressionProcedure();
            grammarRules.add("OutExp -> Expression");
            buildTree("integer", 1);
        } else {
            throw new ParserException("");
        }
    }

    private void StringNodeProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "StringNode" + "\033[0m");
        readToken(TokenType.String);
//        grammarRules.add("StringNode -> '<string>';");
    }

    private int CaseclausesProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Caseclauses" + "\033[0m");
        CaseClauseProcedure();
        int m = 1;
        readToken(TokenType.Predefined_Operator, ";");
//        grammarRules.add("Caseclauses-> (Caseclause ';')+");
        while (nextToken().type == TokenType.Integer || nextToken().type == TokenType.Char
                || nextToken().type == TokenType.Identifier) {
            CaseClauseProcedure();
            m++;
            readToken(TokenType.Predefined_Operator, ";");
        }
        return m;
    }

    private void CaseClauseProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Caseclause" + "\033[0m");
        CaseExpressionProcedure();
        int n = 1;
        while (Objects.equals(nextToken().value, ",")) {
            readToken(TokenType.Predefined_Operator, ",");
            CaseExpressionProcedure();
            n++;
        }
        readToken(TokenType.Predefined_Operator, ":");
        StatementProcedure();
        grammarRules.add("Caseclause -> CaseExpression list ',' ':' Statement => \"case_clause\"");
        buildTree("case_clause", n + 1);
    }

    private void CaseExpressionProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "CaseExpression" + "\033[0m");
        ConstValueProcedure();
        if (Objects.equals(nextToken().value, "..")) {
            readToken(TokenType.Predefined_Operator, "..");
            ConstValueProcedure();
            grammarRules.add("CaseExpression -> ConstValue '..' ConstValue");
            buildTree("..", 2);
        }else{
//            grammarRules.add("CaseExpression -> ConstValue");
        }
    }

    private int OtherwiseClauseProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Otherwise" + "\033[0m");
        int m = 0;
        if (Objects.equals(nextToken().value, "otherwise")) {
            readToken(TokenType.Predefined_Keyword, "otherwise");
            StatementProcedure();
            m++;
            grammarRules.add("OtherwiseClause -> 'otherwise' Statement");
            buildTree("otherwise", 1);
        }else {
//            grammarRules.add("OtherwiseClause -> ");
        }
        return m;
    }

    private void AssignmentProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Assignment" + "\033[0m");
        NameProcedure();
        if (Objects.equals(nextToken().value, ":=")) {
            readToken(TokenType.Predefined_Operator, ":=");
            ExpressionProcedure();
            grammarRules.add("Assignment -> Name ':=' Expression");
            buildTree("assign", 2);
        } else if (Objects.equals(nextToken().value, ":=:")) {
            readToken(TokenType.Predefined_Operator, ":=:");
            NameProcedure();
            grammarRules.add("Assignment -> Name ':=:' Name");
            buildTree("swap", 2);
        } else {
            throw new ParserException("");
        }
    }

    private void ForStatProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ForStat" + "\033[0m");
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
//            grammarRules.add("ForStat -> Assignment");
        } else {
            grammarRules.add("ForStat -> ");
            buildTree("<null>", 0);
        }
    }

    private void ForExpProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ForExp" + "\033[0m");
        if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char
                || nextToken().type == TokenType.Integer ||
                new ArrayList<>(Arrays.asList("+", "-", "(", "not", "eof", "succ", "pred", "chr", "ord"))
                        .contains(nextToken().value)) {
            ExpressionProcedure();
//            grammarRules.add("ForExp -> Expression");
        } else {
            grammarRules.add("ForExp -> ");
            buildTree("true", 0);
        }
    }

    private void ExpressionProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Expression" + "\033[0m");
        TermProcedure();
        if (Objects.equals(nextToken().value, "<=")) {
            readToken(TokenType.Predefined_Operator, "<=");
            TermProcedure();
            grammarRules.add("Expression -> Term '<=' Term");
            buildTree("<=", 2);
        } else if (Objects.equals(nextToken().value, "<")) {
            readToken(TokenType.Predefined_Operator, "<");
            TermProcedure();
            grammarRules.add("Expression -> Term '<' Term");
            buildTree("<", 2);
        } else if (Objects.equals(nextToken().value, ">=")) {
            readToken(TokenType.Predefined_Operator, ">=");
            TermProcedure();
            grammarRules.add("Expression -> Term '>=' Term");
            buildTree(">=", 2);
        } else if (Objects.equals(nextToken().value, ">")) {
            readToken(TokenType.Predefined_Operator, ">");
            TermProcedure();
            grammarRules.add("Expression -> Term '>' Term");
            buildTree(">", 2);
        } else if (Objects.equals(nextToken().value, "=")) {
            readToken(TokenType.Predefined_Operator, "=");
            TermProcedure();
            grammarRules.add("Expression -> Term '=' Term");
            buildTree("=", 2);
        } else if (Objects.equals(nextToken().value, "<>")) {
            readToken(TokenType.Predefined_Operator, "<>");
            TermProcedure();
            grammarRules.add("Expression -> Term '<>' Term");
            buildTree("<>", 2);
        } else{
//            grammarRules.add("Expression -> Term");
        }
    }

    private void TermProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Term" + "\033[0m");
        FactorProcedure();
        while (new ArrayList<>(Arrays.asList("+", "-", "or")).contains(nextToken().value)) {
            if (Objects.equals(nextToken().value, "+")) {
                readToken(TokenType.Predefined_Operator, "+");
                FactorProcedure();
                grammarRules.add("Term -> Term '+' Factor");
                buildTree("+", 2);
            } else if (Objects.equals(nextToken().value, "-")) {
                readToken(TokenType.Predefined_Operator, "-");
                FactorProcedure();
                grammarRules.add("Term -> Term '-' Factor");
                buildTree("-", 2);
            } else if (Objects.equals(nextToken().value, "or")) {
                readToken(TokenType.Predefined_Keyword, "or");
                FactorProcedure();
                grammarRules.add("Term -> Term 'or' Factor");
                buildTree("or", 2);
            } else{
//                grammarRules.add("Term -> Factor");
            }
        }
    }

    private void FactorProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Factor" + "\033[0m");
        PrimaryProcedure();
        while (new ArrayList<>(Arrays.asList("*", "/", "and", "mod")).contains(nextToken().value)) {
            if (Objects.equals(nextToken().value, "*")) {
                readToken(TokenType.Predefined_Operator, "*");
                PrimaryProcedure();
                grammarRules.add("Factor -> Factor '*' Primary");
                buildTree("*", 2);
            } else if (Objects.equals(nextToken().value, "/")) {
                readToken(TokenType.Predefined_Operator, "/");
                PrimaryProcedure();
                grammarRules.add("Factor -> Factor '/' Primary");
                buildTree("/", 2);
            } else if (Objects.equals(nextToken().value, "and")) {
                readToken(TokenType.Predefined_Keyword, "and");
                PrimaryProcedure();
                grammarRules.add("Factor -> Factor 'and' Primary");
                buildTree("and", 2);
            } else if (Objects.equals(nextToken().value, "mod")) {
                readToken(TokenType.Predefined_Keyword, "mod");
                PrimaryProcedure();
                grammarRules.add("Factor -> Factor 'mod' Primary");
                buildTree("mod", 2);
            }else{
//                grammarRules.add("Factor -> Primary");
            }
        }
    }

    private void PrimaryProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Primary" + "\033[0m");
        if (nextToken().type == TokenType.Integer) {
            readToken(TokenType.Integer);
//            grammarRules.add("Primary -> '<integer>'");
        } else if (nextToken().type == TokenType.Char) {
            readToken(TokenType.Char);
//            grammarRules.add("Primary -> '<char>'");
        } else if (Objects.equals(nextToken().value, "-")) {
            readToken(TokenType.Predefined_Operator, "-");
            PrimaryProcedure();
            grammarRules.add("Primary -> '-' Primary");
            buildTree("-", 1);
        } else if (Objects.equals(nextToken().value, "+")) {
            readToken(TokenType.Predefined_Operator, "+");
            PrimaryProcedure();
//            grammarRules.add("Primary -> '+' Primary");
        } else if (Objects.equals(nextToken().value, "not")) {
            readToken(TokenType.Predefined_Keyword, "not");
            PrimaryProcedure();
            grammarRules.add("Primary -> 'not' Primary");
            buildTree("not", 1);
        } else if (Objects.equals(nextToken().value, "eof")) {
            readToken(TokenType.Predefined_Keyword, "eof");
            grammarRules.add("Primary -> 'eof'");
            buildTree("eof", 0);
        } else if (nextToken().type == TokenType.Identifier) {
            NameProcedure();
            int n = 1;
            if (Objects.equals(nextToken().value, "(")) {
                readToken(TokenType.Predefined_Operator, "(");
                ExpressionProcedure();
                n++;
                while (Objects.equals(nextToken().value, ",")) {
                    readToken(TokenType.Predefined_Operator, ",");
                    ExpressionProcedure();
                    n++;
                }
                readToken(TokenType.Predefined_Operator, ")");
                grammarRules.add("Primary -> Name '(' Expression list ',' ')'");
                buildTree("call", n);
            }else{
//                grammarRules.add("Primary -> Name");
            }
        } else if (Objects.equals(nextToken().value, "(")) {
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
//            grammarRules.add("Primary -> '(' Expression ')'");
        } else if (Objects.equals(nextToken().value, "succ")) {
            readToken(TokenType.Predefined_Keyword, "succ");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            grammarRules.add("'succ' '(' Expression ')'");
            buildTree("succ", 1);
        } else if (Objects.equals(nextToken().value, "pred")) {
            readToken(TokenType.Predefined_Keyword, "pred");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            grammarRules.add("'pred' '(' Expression ')'");
            buildTree("pred", 1);
        } else if (Objects.equals(nextToken().value, "chr")) {
            readToken(TokenType.Predefined_Keyword, "chr");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            grammarRules.add("'chr' '(' Expression ')'");
            buildTree("chr", 1);
        } else if (Objects.equals(nextToken().value, "ord")) {
            readToken(TokenType.Predefined_Keyword, "ord");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            grammarRules.add("'ord' '(' Expression ')'");
            buildTree("ord", 1);
        } else {
            throw new ParserException("");
        }
    }

    private void NameProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Name" + "\033[0m");
        if (nextToken().type == TokenType.Identifier) {
            readToken(TokenType.Identifier);
//            grammarRules.add("Name -> '<identifier>'");
        } else {
            throw new ParserException("");
        }
    }

    private Token nextToken() {
        if (tokens.size() > 0) {
            return tokens.get(0);
        } else {
            return new Token(TokenType.Identifier, "");
        }
    }

    private void readToken(TokenType t, String v) {
        if (nextToken().type == t && Objects.equals(nextToken().value, v)) {
            if (nextToken().type == TokenType.Identifier) {
                treeNodes.push(new Node("<identifier>", new Node(nextToken().value, null, null, 0), null, 1));
            } else if (nextToken().type == TokenType.Integer) {
                treeNodes.push(new Node("<integer>", new Node(nextToken().value, null, null, 0), null, 1));
            } else if (nextToken().type == TokenType.Char) {
                treeNodes.push(new Node("<char>", new Node(nextToken().value, null, null, 0), null, 1));
            } else if (nextToken().type == TokenType.String) {
                treeNodes.push(new Node("<string>", new Node(nextToken().value, null, null, 0), null, 1));
            }
            if (debugProcedures) {
                System.out.println("Consumed : " + tokens.remove(0).visualize());
            } else {
                tokens.remove(0);
            }
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at "
                    + v.toUpperCase() + "\033[0m");
        }
    }

    private void readToken(TokenType t) {
        if (nextToken().type == t) {
            if (nextToken().type == TokenType.Identifier) {
                treeNodes.push(new Node("<identifier>", new Node(nextToken().value, null, null, 0), null, 1));
                // System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t +
                // "\033[0m");
            } else if (nextToken().type == TokenType.Integer) {
                treeNodes.push(new Node("<integer>", new Node(nextToken().value, null, null, 0), null, 1));
                // System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t +
                // "\033[0m");
            } else if (nextToken().type == TokenType.Char) {
                treeNodes.push(new Node("<char>", new Node(nextToken().value, null, null, 0), null, 1));
                // System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t +
                // "\033[0m");
            } else if (nextToken().type == TokenType.String) {
                treeNodes.push(new Node("<string>", new Node(nextToken().value, null, null, 0), null, 1));
                // System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t +
                // "\033[0m");
            }
            if (debugProcedures) {
                System.out.println("Consumed : " + tokens.remove(0).visualize());
            } else {
                tokens.remove(0);
            }
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at "
                    + t.toString().toUpperCase() + "\033[0m");
        }
    }

    private void buildTree(String name, int childCount) {
        Node p = null;
        StringBuilder det = new StringBuilder();
        for (int i = 0; i < childCount; i++) {
            Node c = treeNodes.pop();
            det.append(" : ").append(c.name);
            c.right = p;
            p = c;
        }
        treeNodes.push(new Node(name, p, null, childCount));
        if (debugProcedures)
            System.out.println("\033[35;0m" + det + " --> pushed into " + name + "\033[0m");

    }

    public void visualizeTree() {
        if (debugProcedures) {
            int size = treeNodes.size();
            System.out.println("Number of nodes in Stack : " + treeNodes.size());
            for (int i = 0; i < size; i++) {
                visit(treeNodes.pop(), 0);
            }
        } else {
            visit(treeNodes.pop(), 0);
        }
    }

    public void writeTree(String filePath) {
        try {
            File file = new File(filePath);
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            visit(treeNodes.pop(), 0);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void visit(Node n, int level) {
        // fw.write(n.visualize(level));
        // fw.newLine();
        System.out.println(n.visualize(level));
        if (n.left != null) {
            visit(n.left, level + 1);
        }
        if (n.right != null) {
            visit(n.right, level);
        }
    }

}
