package main.java;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeGenerator {

    private Node rootNode;
    private ArrayList<String> code = new ArrayList<>();
    private ArrayList<String> errors = new ArrayList<>();
    private HashMap<String, Integer> globalLookupTable = new HashMap<>();
    private HashMap<String, VariableType> globalVariableTypes = new HashMap<>();
    private HashMap<String, Environment> environments = new HashMap<>();
    private String environmentName = "global";
    private int environmentCounter = 1;
    private String prevEnvName = "";

    void initialize(Node r) {
        rootNode = r;
        Evaluate(r, new AttributePanel(0), null);
        System.out.println("codes :" + code.size());
        for (String s : code) {
            System.out.println(s);
        }
        System.out.println("errors :" + errors.size());
        for (String e : errors) {
            System.out.println(e);
        }
    }

    private Environment createConditionalEnvironment(int stackBottom, Environment functionalParent) {
        Environment env = new Environment(stackBottom, environmentCounter, functionalParent);
        environmentCounter++;
        return env;
    }

    private Environment createFunctionalEnvironment(int stackBottom) {
        Environment env = new Environment(stackBottom, environmentCounter);
        environmentCounter++;
        return env;
    }

    private void updateEnvironmentName(Environment currentEnv, int newNumber) {
        if (currentEnv == null) {
            environmentName = "L" + newNumber;
        } else {
            currentEnv.envName = "L" + newNumber;
        }
    }


    private boolean isIdentifierValid(String identifier, Environment env) {
        if (env == null) {
            return globalLookupTable.containsKey(identifier);
        } else {
            return env.localLookupTable.containsKey(identifier) || globalLookupTable.containsKey(identifier);
        }
    }

    private String generateLoadIdentifierLine(String identifier, Environment env) {
        if ((env != null && env.functionParent != null) && env.localLookupTable.containsKey(identifier)) {
            return "LLV\t" + env.localLookupTable.get(identifier);
        } else if (globalLookupTable.containsKey(identifier)) {
            return "LGV\t" + globalLookupTable.get(identifier);
        } else {
            return "ERROR";
        }
    }

    private String generateReassignIdentifierLine(String identifier, Environment env) {
        if ((env != null && env.functionParent != null) && env.localLookupTable.containsKey(identifier)) {
            return "SLV\t" + env.localLookupTable.get(identifier);
        } else if (globalLookupTable.containsKey(identifier)) {
            return "SGV\t" + globalLookupTable.get(identifier);
        } else {
            return "ERROR";
        }
    }

    private String generateDefineIdentifierLine(String identifier, Environment env, int stackPointer, VariableType type) {
        if ((env != null && env.functionParent != null) && !env.localLookupTable.containsKey(identifier)) {
            env.localLookupTable.put(identifier, stackPointer);
            env.localVariableTypes.put(identifier, type);
            return "LIT\t0";
        } else if (!globalLookupTable.containsKey(identifier)) {
            globalLookupTable.put(identifier, stackPointer);
            globalVariableTypes.put(identifier, type);
            return "LIT\t0";
        } else {
            return "ERROR";
        }
    }

    private int retrieveIdentifierIndex(String identifier, Environment env) {
        if ((env != null && env.functionParent != null) && env.localLookupTable.containsKey(identifier)) {
            return env.localLookupTable.get(identifier);
        } else if (globalLookupTable.containsKey(identifier)) {
            return globalLookupTable.get(identifier);
        } else {
            return -1;
        }
    }

    private VariableType retrieveVariableType(String identifier, Environment env) {
        if ((env != null && env.functionParent != null) && env.localLookupTable.containsKey(identifier)) {
            return env.localVariableTypes.get(identifier);
        } else if (globalLookupTable.containsKey(identifier)) {
            return globalVariableTypes.get(identifier);
        } else {
            return VariableType.Undecided;
        }
    }

    private VariableType getVariableTypeFromString(String var) {
        switch (var) {
            case "integer":
                return VariableType.Integer;
            case "char":
                return VariableType.Char;
            case "string":
                return VariableType.String;
            default:
                return VariableType.Undecided;
        }
    }

    private void generateCode(String codeLine, Environment currentEnvironment) {
        String env = "";
        if (currentEnvironment != null) {
            env = currentEnvironment.envName;
        } else {
            env = environmentName;
        }
        if (prevEnvName.equals(env)) {
            code.add("\t" + codeLine);
        } else if (!env.equals("global")) {
            code.add(env + "\t" + codeLine);
            prevEnvName = env;
        } else {
            code.add("\t" + codeLine);
            prevEnvName = env;
        }
    }

    private void generateCode(String op, String operand, Environment currentEnvironment) {
        String env = "";
        if (currentEnvironment != null) {
            env = currentEnvironment.envName;
        } else {
            env = environmentName;
        }
        if (prevEnvName.equals(env)) {
            code.add("\t" + op + "\t" + operand);
        } else if (!env.equals("global")) {
            code.add(env + "\t" + op + "\t" + operand);
            prevEnvName = env;
        } else {
            code.add("\t" + op + "\t" + operand);
            prevEnvName = env;
        }
    }

    private AttributePanel Evaluate(Node node, AttributePanel attr, Environment currentEnvironment) {
        switch (node.name) {
            case "<integer>":
                return _integer_Handler(node, attr, currentEnvironment);
            case "<identifier>":
                return _identifier_Handler(node, attr, currentEnvironment);
            case "+":
                return plusHandler(node, attr, currentEnvironment);
            case "-":
                if (node.childrenCount == 1) {
                    return negHandler(node, attr, currentEnvironment);
                } else {
                    return minusHandler(node, attr, currentEnvironment);
                }
            case "*":
                return multiHandler(node, attr, currentEnvironment);
            case "/":
                return divHandler(node, attr, currentEnvironment);
            case "mod":
                return modHandler(node, attr, currentEnvironment);
            case "and":
                return andHandler(node, attr, currentEnvironment);
            case "or":
                return orHandler(node, attr, currentEnvironment);
            case "succ":
                return succHandler(node, attr, currentEnvironment);
            case "pred":
                return predHandler(node, attr, currentEnvironment);
            case "=":
                return equalHandler(node, attr, currentEnvironment);
            case "<>":
                return neqHandler(node, attr, currentEnvironment);
            case ">":
                return grtHandler(node, attr, currentEnvironment);
            case "<":
                return lstHandler(node, attr, currentEnvironment);
            case ">=":
                return gteHandler(node, attr, currentEnvironment);
            case "<=":
                return lteHandler(node, attr, currentEnvironment);
            case "assign":
                return assignHandler(node, attr, currentEnvironment);
            case "var":
                return varHandler(node, attr, currentEnvironment);
            case "integer":
                return integerHandler(node, attr, currentEnvironment);
            case "if":
                return ifHandler(node, attr, currentEnvironment);
            case "while":
                return whileHandler(node, attr, currentEnvironment);
            case "output":
                return outputHandler(node, attr, currentEnvironment);
            case "read":
                return readHandler(node, attr, currentEnvironment);
            case "block":
                return blockHandler(node, attr, currentEnvironment);
//            case "subprogs":
//                return subprogsHandler(node, attr, currentEnvironment);
//            case "dclns":
//                return dclnsHandler(node, attr, currentEnvironment);
//            case "types":
//                return typesHandler(node, attr, currentEnvironment);
//            case "consts":
//                return constsHandler(node, attr, currentEnvironment);
//            case "program":
////                return programHandler(node, attr, currentEnvironment);
//                AttributePanel attributePanel = attr;
//                for (int i = 1; i < node.childrenCount-1; i++) {
//                    attributePanel = Evaluate(node.getChild(i),attributePanel,currentEnvironment);
//                }
//                return attributePanel;
            default:
                return defaultHandler(node, attr, currentEnvironment);

        }

    }


    private AttributePanel defaultHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel attr = parentAttr;
        for (int i = 0; i < n.childrenCount; i++) {
            attr = Evaluate(n.getChild(i), attr, env);
        }
        return attr;
    }


    //=================== FUNCTIONAL NODES =======================

    private AttributePanel subprogsHandler(Node n, AttributePanel parentAttr, Environment env) {
        return null;
    }

    private AttributePanel dclnsHandler(Node n, AttributePanel parentAttr, Environment env) {
        return null;
    }

    private AttributePanel typesHandler(Node n, AttributePanel parentAttr, Environment env) {
        return null;
    }

    private AttributePanel constsHandler(Node n, AttributePanel parentAttr, Environment env) {
        return null;
    }

    private AttributePanel programHandler(Node n, AttributePanel parentAttr, Environment env) {
        return null;
    }


    //===================== SUPPORTIVE NODES ========================

    private AttributePanel integerHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel expAttr = Evaluate(n.getChild(0), parentAttr, env);
        if (expAttr.variableType != VariableType.Integer) {
            errors.add("IntergerNode child type not Integer");
            return new AttributePanel(expAttr.stackSize, VariableType.Undecided);
        } else {
            return new AttributePanel(expAttr.stackSize, VariableType.Integer);
        }
    }

    private AttributePanel blockHandler(Node n, AttributePanel parentAttr, Environment env) {
        return defaultHandler(n, parentAttr, env);
    }

    private AttributePanel varHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel attr = parentAttr;
        VariableType idType = getVariableTypeFromString(n.getChild(n.childrenCount - 1).getChild(0).name);
        for (int i = 0; i < n.childrenCount - 1; i++) {
            if (idType == VariableType.Undecided) {
                errors.add("<identifier> " + n.getChild(i).getChild(0).name + " type not valid");
                attr = new AttributePanel(attr.stackSize, VariableType.Undecided);
            } else {
                String line = generateDefineIdentifierLine(n.getChild(i).getChild(0).name, env, attr.stackSize, idType);
                if (line.equals("ERROR")) {
                    errors.add("<identifier> " + n.getChild(i).getChild(0).name + " is already defined in the scope");
                    attr = new AttributePanel(attr.stackSize, VariableType.Undecided);
                } else {
                    generateCode(line, env);
                    attr = new AttributePanel(attr.stackSize + 1, idType);
                }
            }
        }
        return attr;
    }

    private AttributePanel assignHandler(Node n, AttributePanel parentAttr, Environment env) {
        int identifierIndex = retrieveIdentifierIndex(n.getChild(0).getChild(0).name, env);
        AttributePanel expAttr = Evaluate(n.getChild(1), parentAttr, env);
        if (identifierIndex < 0) {
            errors.add("ASSIGN <identifier> " + n.getChild(0).getChild(0).name + " is undefined");
            return new AttributePanel(expAttr.stackSize - 1, VariableType.Undecided);
        } else {
            generateCode(generateReassignIdentifierLine(n.getChild(0).getChild(0).name, env), env);
            return new AttributePanel(expAttr.stackSize, VariableType.Undecided);
        }
    }


    //================= STATEMENT NODES ======================

    private AttributePanel ifHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel expAttr = Evaluate(n.getChild(0), parentAttr, env);
        if (expAttr.variableType == VariableType.Boolean) {
            Environment thenEnv = createConditionalEnvironment(expAttr.stackSize, env != null ? env.functionParent : null);
            Environment elseEnv = createConditionalEnvironment(expAttr.stackSize, env != null ? env.functionParent : null);
            generateCode("COND\t" + thenEnv.envName + "\t" + elseEnv.envName, env);
            AttributePanel thenAttr = Evaluate(n.getChild(1), expAttr, thenEnv);
            updateEnvironmentName(env, environmentCounter);
            generateCode("GOTO", "L" + environmentCounter, thenEnv);
            environmentCounter++;
            AttributePanel elseAttr;
            if (n.childrenCount > 2) {
                elseAttr = Evaluate(n.getChild(2), thenAttr, elseEnv);
            } else {
                generateCode("NOP", elseEnv);
                elseAttr = new AttributePanel(thenAttr.stackSize, thenAttr.variableType);
            }
            generateCode("NOP", env);
            return elseAttr;
        } else {
            errors.add("IF expression does not return a boolean value");
            return new AttributePanel(expAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel whileHandler(Node n, AttributePanel parentAttr, Environment env) {
//        int returningEnvNum = environmentCounter;
//        environmentCounter++;
        Environment expEnv = createConditionalEnvironment(parentAttr.stackSize, env != null ? env.functionParent : null);
        AttributePanel expAttr = Evaluate(n.getChild(0), parentAttr, expEnv);
        Environment doEnv = createConditionalEnvironment(expAttr.stackSize, env != null ? env.functionParent : null);
        Environment elseEnv = createConditionalEnvironment(expAttr.stackSize, env != null ? env.functionParent : null);
        generateCode("COND\t" + doEnv.envName + "\t" + elseEnv.envName, expEnv);
        AttributePanel doAttr = Evaluate(n.getChild(1), expAttr, doEnv);
        generateCode("GOTO", expEnv.envName, doEnv);
//        updateEnvironmentName(env, returningEnvNum);
        generateCode("NOP", elseEnv);
        AttributePanel elseAttr = new AttributePanel(doAttr.stackSize, doAttr.variableType);
//        generateCode("NOP", env);
        return elseAttr;
    }

    private AttributePanel outputHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel attr = parentAttr;
        for (int i = 0; i < n.childrenCount; i++) {
            attr = Evaluate(n.getChild(i), attr, env);
            generateCode("SOS", "OUTPUT", env);
            generateCode("SOS", "OUTPUTL", env);
            attr = new AttributePanel(attr.stackSize - 1, attr.variableType);
        }
        return attr;
    }

    private AttributePanel readHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel attr = parentAttr;
        for (int i = 0; i < n.childrenCount; i++) {
            generateCode("SOS", "INPUT", env);
            int identifierIndex = retrieveIdentifierIndex(n.getChild(i).getChild(0).name, env);
            VariableType type = retrieveVariableType(n.getChild(i).getChild(0).name, env);
            if (identifierIndex < 0) {
                errors.add("READ <identifier> " + n.getChild(i).getChild(0).name + " is undefined");
                attr = new AttributePanel(attr.stackSize + 1, VariableType.Undecided);
            } else {
                generateCode(generateReassignIdentifierLine(n.getChild(i).getChild(0).name, env), env);
                attr = new AttributePanel(attr.stackSize, type);
            }
        }
        return attr;
    }


    //================= COMPARISON NODES =====================

    private AttributePanel equalHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == rightAttr.variableType) {
            generateCode("BOP", "BEQ", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("EQUAL values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel neqHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == rightAttr.variableType) {
            generateCode("BOP", "BNE", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("NOT EQUAL values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel grtHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == rightAttr.variableType) {
            generateCode("BOP", "BGT", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("GREATER values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel lstHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == rightAttr.variableType) {
            generateCode("BOP", "BLT", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("LESS values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel gteHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == rightAttr.variableType) {
            generateCode("BOP", "BGE", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("GTorEQ values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel lteHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == rightAttr.variableType) {
            generateCode("BOP", "BLE", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("LSorEQ values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }


    //================== ARITHMETIC NODES =====================

    private AttributePanel plusHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
            generateCode("BOP", "BPLUS", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Integer);
        } else {
            errors.add("PLUS non-integer values cannot be added");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel minusHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
            generateCode("BOP", "BMINUS", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Integer);
        } else {
            errors.add("MINUS non-integer values cannot be subtracted");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel multiHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
            generateCode("BOP", "BMULT", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Integer);
        } else {
            errors.add("MULTI non-integer values cannot be multiplied");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel divHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
            generateCode("BOP", "BDIV", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Integer);
        } else {
            errors.add("DIV non-integer values cannot be divided");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel modHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
            generateCode("BOP", "BMOD", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Integer);
        } else {
            errors.add("MOD non-integer values cannot be modulated");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }


    //=================== LOGICAL NODES =====================

    private AttributePanel andHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Boolean && rightAttr.variableType == VariableType.Boolean) {
            generateCode("BOP", "BAND", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("AND non-bool values cannot be and");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel orHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Boolean && rightAttr.variableType == VariableType.Boolean) {
            generateCode("BOP", "BOR", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("MINUS non-bool values cannot be or");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }


    //=================== SINGLE OPERATION NODES =====================

    private AttributePanel negHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel childAttr = Evaluate(n.getChild(0), parentAttr, env);
        if (childAttr.variableType == VariableType.Integer) {
            generateCode("UOP", "UNOT", env);
            return new AttributePanel(childAttr.stackSize, VariableType.Integer);
        } else {
            errors.add("NEG non-integer values cannot be negated");
            return new AttributePanel(childAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel succHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel childAttr = Evaluate(n.getChild(0), parentAttr, env);
        if (childAttr.variableType == VariableType.Integer) {
            generateCode("UOP", "USUCC", env);
            return new AttributePanel(childAttr.stackSize, VariableType.Integer);
        } else {
            errors.add("SUCC non-integer values cannot be succeeded");
            return new AttributePanel(childAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel predHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel childAttr = Evaluate(n.getChild(0), parentAttr, env);
        if (childAttr.variableType == VariableType.Integer) {
            generateCode("UOP", "UPRED", env);
            return new AttributePanel(childAttr.stackSize, VariableType.Integer);
        } else {
            errors.add("PRED non-integer values cannot be preceded");
            return new AttributePanel(childAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel notHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel childAttr = Evaluate(n.getChild(0), parentAttr, env);
        if (childAttr.variableType == VariableType.Boolean) {
            generateCode("UOP", "UNOT", env);
            return new AttributePanel(childAttr.stackSize, VariableType.Boolean);
        } else {
            errors.add("NOT non-boolean values cannot be complimented");
            return new AttributePanel(childAttr.stackSize, VariableType.Undecided);
        }
    }


    //=================== LEAF NODES =======================

    private AttributePanel _integer_Handler(Node n, AttributePanel parentAttr, Environment env) {
        generateCode("LIT", n.getChild(0).name, env);
        return new AttributePanel(parentAttr.stackSize + 1, VariableType.Integer);
    }

    private AttributePanel _identifier_Handler(Node n, AttributePanel parentAttr, Environment env) {
        String line = generateLoadIdentifierLine(n.getChild(0).name, env);
        VariableType idType = retrieveVariableType(n.getChild(0).name, env);
        if (line.equals("ERROR")) {
            errors.add("<identifier> " + n.getChild(0).name + " is undefined");
        } else if (idType == VariableType.Undecided) {
            errors.add("<identifier> " + n.getChild(0).name + " type not defined");
        } else {
            generateCode(line, env);
            return new AttributePanel(parentAttr.stackSize + 1, idType);
        }
        return new AttributePanel(parentAttr.stackSize, VariableType.Undecided);
    }

}
