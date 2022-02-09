import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

class Parser {

    private ArrayList<Token> tokens;
    private boolean debugProcedures = false;
    private Stack<Node> treeNodes = new Stack<>();

    void parse(ArrayList<Token> t) {
        tokens = (ArrayList<Token>) t.clone();
        try {
            WinzigProcedure();
        } catch (ParserException e) {
            System.out.println(nextToken().type + " : " + nextToken().value);
            e.printStackTrace();
        }
        visualizeTree();
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
            buildTree("consts", n);

        } else {
            buildTree("consts", 0);
        }
    }

    private void ConstProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Const" + "\033[0m");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        ConstValueProcedure();
        buildTree("const", 2);
    }

    private void ConstValueProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ConstValue" + "\033[0m");
        if (nextToken().type == TokenType.Integer) {
            readToken(TokenType.Integer);
        } else if (nextToken().type == TokenType.Char) {
            readToken(TokenType.Char);
        } else if (nextToken().type == TokenType.Identifier) {
            NameProcedure();
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
            buildTree("types", n);
        } else {
            buildTree("types", 0);
        }
    }

    private void TypeProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Type" + "\033[0m");
        NameProcedure();
        readToken(TokenType.Predefined_Operator, "=");
        LitListProcedure();
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
            buildTree("dclns", n);
        } else {
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
            }
        }
        readToken(TokenType.Predefined_Keyword, "end");
        buildTree("block", n);
    }

    private void StatementProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Statement" + "\033[0m");
        if (nextToken().type == TokenType.Identifier) {
            AssignmentProcedure();
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
            buildTree("if", n);
        } else if (Objects.equals(nextToken().value, "while")) {
            readToken(TokenType.Predefined_Keyword, "while");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "do");
            StatementProcedure();
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
            buildTree("loop", n);
        } else if (Objects.equals(nextToken().value, "case")) {
            readToken(TokenType.Predefined_Keyword, "case");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Keyword, "of");
            int caseCount = CaseclausesProcedure();
            int otherwise = OtherwiseClauseProcedure();
            readToken(TokenType.Predefined_Keyword, "end");
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
            buildTree("read", n);
        } else if (Objects.equals(nextToken().value, "exit")) {
            readToken(TokenType.Predefined_Keyword, "exit");
            buildTree("exit", 0);
        } else if (Objects.equals(nextToken().value, "return")) {
            readToken(TokenType.Predefined_Keyword, "return");
            ExpressionProcedure();
            buildTree("return", 1);
        } else if (Objects.equals(nextToken().value, "begin")) {
            BodyProcedure();
        } else {
            buildTree("<null>", 0);
        }
    }

    private void OutExpProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "OutExp" + "\033[0m");
        if (nextToken().type == TokenType.String) {
            StringNodeProcedure();
            buildTree("string", 1);
        } else if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char || nextToken().type == TokenType.Integer ||
                new ArrayList<>(Arrays.asList("+", "-", "(", "not", "eof", "succ", "pred", "chr", "ord")).contains(nextToken().value)) {
            ExpressionProcedure();
            buildTree("integer", 1);
        } else {
            throw new ParserException("");
        }
    }

    private void StringNodeProcedure() {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "StringNode" + "\033[0m");
        readToken(TokenType.String);
    }

    private int CaseclausesProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Caseclauses" + "\033[0m");
        CaseClauseProcedure();
        int m = 1;
        readToken(TokenType.Predefined_Operator, ";");
        while (nextToken().type == TokenType.Integer || nextToken().type == TokenType.Char || nextToken().type == TokenType.Identifier) {
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
        buildTree("case_clause", n + 1);
    }

    private void CaseExpressionProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "CaseExpression" + "\033[0m");
        ConstValueProcedure();
        if (Objects.equals(nextToken().value, "..")) {
            readToken(TokenType.Predefined_Operator, "..");
            ConstValueProcedure();
            buildTree("..", 2);
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
            buildTree("otherwise", 1);
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
            buildTree("assign", 2);
        } else if (Objects.equals(nextToken().value, ":=:")) {
            readToken(TokenType.Predefined_Operator, ":=:");
            NameProcedure();
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
        } else {
            buildTree("<null>", 0);
        }
    }

    private void ForExpProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "ForExp" + "\033[0m");
        if (nextToken().type == TokenType.Identifier || nextToken().type == TokenType.Char || nextToken().type == TokenType.Integer ||
                new ArrayList<>(Arrays.asList("+", "-", "(", "not", "eof", "succ", "pred", "chr", "ord")).contains(nextToken().value)) {
            ExpressionProcedure();
        } else {
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
            buildTree("<=", 2);
        } else if (Objects.equals(nextToken().value, "<")) {
            readToken(TokenType.Predefined_Operator, "<");
            TermProcedure();
            buildTree("<", 2);
        } else if (Objects.equals(nextToken().value, ">=")) {
            readToken(TokenType.Predefined_Operator, ">=");
            TermProcedure();
            buildTree(">=", 2);
        } else if (Objects.equals(nextToken().value, ">")) {
            readToken(TokenType.Predefined_Operator, ">");
            TermProcedure();
            buildTree(">", 2);
        } else if (Objects.equals(nextToken().value, "=")) {
            readToken(TokenType.Predefined_Operator, "=");
            TermProcedure();
            buildTree("=", 2);
        } else if (Objects.equals(nextToken().value, "<>")) {
            readToken(TokenType.Predefined_Operator, "<>");
            TermProcedure();
            buildTree("<>", 2);
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
                buildTree("+", 2);
            } else if (Objects.equals(nextToken().value, "-")) {
                readToken(TokenType.Predefined_Operator, "-");
                FactorProcedure();
                buildTree("-", 2);
            } else if (Objects.equals(nextToken().value, "or")) {
                readToken(TokenType.Predefined_Keyword, "or");
                FactorProcedure();
                buildTree("or", 2);
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
                buildTree("*", 2);
            } else if (Objects.equals(nextToken().value, "/")) {
                readToken(TokenType.Predefined_Operator, "/");
                PrimaryProcedure();
                buildTree("/", 2);
            } else if (Objects.equals(nextToken().value, "and")) {
                readToken(TokenType.Predefined_Keyword, "and");
                PrimaryProcedure();
                buildTree("and", 2);
            } else if (Objects.equals(nextToken().value, "mod")) {
                readToken(TokenType.Predefined_Keyword, "mod");
                PrimaryProcedure();
                buildTree("mod", 2);
            }
        }
    }

    private void PrimaryProcedure() throws ParserException {
        if (debugProcedures)
            System.out.println("\033[33;0m" + "Primary" + "\033[0m");
        if (nextToken().type == TokenType.Integer) {
            readToken(TokenType.Integer);
        } else if (nextToken().type == TokenType.Char) {
            readToken(TokenType.Char);
        } else if (Objects.equals(nextToken().value, "-")) {
            readToken(TokenType.Predefined_Operator, "-");
            PrimaryProcedure();
            buildTree("-", 1);
        } else if (Objects.equals(nextToken().value, "+")) {
            readToken(TokenType.Predefined_Operator, "+");
            PrimaryProcedure();
        } else if (Objects.equals(nextToken().value, "not")) {
            readToken(TokenType.Predefined_Keyword, "not");
            PrimaryProcedure();
            buildTree("not", 1);
        } else if (Objects.equals(nextToken().value, "eof")) {
            readToken(TokenType.Predefined_Keyword, "eof");
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
                buildTree("call", n);
            }
        } else if (Objects.equals(nextToken().value, "(")) {
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
        } else if (Objects.equals(nextToken().value, "succ")) {
            readToken(TokenType.Predefined_Keyword, "succ");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            buildTree("succ", 1);
        } else if (Objects.equals(nextToken().value, "pred")) {
            readToken(TokenType.Predefined_Keyword, "pred");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            buildTree("pred", 1);
        } else if (Objects.equals(nextToken().value, "chr")) {
            readToken(TokenType.Predefined_Keyword, "chr");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
            buildTree("chr", 1);
        } else if (Objects.equals(nextToken().value, "ord")) {
            readToken(TokenType.Predefined_Keyword, "ord");
            readToken(TokenType.Predefined_Operator, "(");
            ExpressionProcedure();
            readToken(TokenType.Predefined_Operator, ")");
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
        } else {
            throw new ParserException("");
        }
    }

    private Token nextToken() {
        if (tokens.size() > 0) {
            return tokens.get(0);
        } else {
            //TODO raise wrong entry command
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
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at " + v.toUpperCase() + "\033[0m");
        }
    }

    private void readToken(TokenType t) {
        if (nextToken().type == t) {
            if (nextToken().type == TokenType.Identifier) {
                treeNodes.push(new Node("<identifier>", new Node(nextToken().value, null, null, 0), null, 1));
//                System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t + "\033[0m");
            } else if (nextToken().type == TokenType.Integer) {
                treeNodes.push(new Node("<integer>", new Node(nextToken().value, null, null, 0), null, 1));
//                System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t + "\033[0m");
            } else if (nextToken().type == TokenType.Char) {
                treeNodes.push(new Node("<char>", new Node(nextToken().value, null, null, 0), null, 1));
//                System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t + "\033[0m");
            } else if (nextToken().type == TokenType.String) {
                treeNodes.push(new Node("<string>", new Node(nextToken().value, null, null, 0), null, 1));
//                System.out.println("\033[35;0m" + nextToken().value +" --> pushed into "+ t + "\033[0m");
            }
            if (debugProcedures) {
                System.out.println("Consumed : " + tokens.remove(0).visualize());
            } else {
                tokens.remove(0);
            }
        } else {
            System.out.println("\033[31;0m" + "ERROR for consuming " + nextToken().value.toUpperCase() + " at " + t.toString().toUpperCase() + "\033[0m");
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

    private void visualizeTree() {
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

    private void visit(Node n, int level) {
        n.visualize(level);
        if (n.left != null) {
            visit(n.left, level + 1);
        }
        if (n.right != null) {
            visit(n.right, level);
        }
    }

}
