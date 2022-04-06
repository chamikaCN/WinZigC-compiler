package main.java;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeGenerator {

    private Node rootNode;
    private ArrayList<String> code = new ArrayList<>();
    private ArrayList<String> errors = new ArrayList<>();
    private HashMap<String, Integer> globalLookupTable = new HashMap<>();
    private HashMap<String, VariableType> globalVariableTypes = new HashMap<>();
    private HashMap<String, Environment> environments = new HashMap<>();
    private String prevEnvName="global";

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
        Environment env = new Environment(stackBottom, functionalParent);
        return env;
    }

    private Environment createFunctionalEnvironment(int stackBottom) {
        Environment env = new Environment(stackBottom);
        return env;
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
            return "LLV " + env.localLookupTable.get(identifier);
        } else if (globalLookupTable.containsKey(identifier)) {
            return "LGV " + globalLookupTable.get(identifier);
        } else {
            return "ERROR";
        }
    }

    private String generateReassignIdentifierLine(String identifier, Environment env) {
        if ((env != null && env.functionParent != null) && env.localLookupTable.containsKey(identifier)) {
            return "SLV " + env.localLookupTable.get(identifier);
        } else if (globalLookupTable.containsKey(identifier)) {
            return "SGV " + globalLookupTable.get(identifier);
        } else {
            return "ERROR";
        }
    }

    private String generateDefineIdentifierLine(String identifier, Environment env, int stackPointer, VariableType type) {
        if ((env != null && env.functionParent != null) && !env.localLookupTable.containsKey(identifier)) {
            env.localLookupTable.put(identifier, stackPointer);
            env.localVariableTypes.put(identifier, type);
            return "LIT 0";
        } else if (!globalLookupTable.containsKey(identifier)) {
            globalLookupTable.put(identifier, stackPointer);
            globalVariableTypes.put(identifier, type);
            return "LIT 0";
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
        String env = "\t";
        if (currentEnvironment != null) {
            env = currentEnvironment.envName;
        }
        if (prevEnvName.equals(env)) {
            code.add("\t " + codeLine);
        } else {
            code.add(env + " " + codeLine);
            prevEnvName = env;
        }
    }

    private AttributePanel Evaluate(Node node, AttributePanel attr, Environment currentEnvironment) {
        switch (node.name) {
            case "<integer>":
                return _integer_Handler(node, attr, currentEnvironment);
            case "<identifier>":
                return _identifier_Handler(node, attr, currentEnvironment);
            case "-":
                return minusHandler(node, attr, currentEnvironment);
            case "assign":
                return assignHandler(node, attr, currentEnvironment);
            case "var":
                return varHandler(node, attr, currentEnvironment);
            case "mod":
                return modHandler(node, attr, currentEnvironment);
            case "=":
                return equalHandler(node, attr, currentEnvironment);
            case ">=":
                return gteHandler(node, attr, currentEnvironment);
            case "integer":
                return integerHandler(node, attr, currentEnvironment);
//            case "if":
//                return ifHandler(node, attr, currentEnvironment);
//            case "while":
//                return whileHandler(node, attr, currentEnvironment);
            case "output":
                return outputHandler(node, attr, currentEnvironment);
            case "read":
                return readHandler(node, attr, currentEnvironment);
//            case "block":
//                return blockHandler(node, attr, currentEnvironment);
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
        AttributePanel expAttr = Evaluate(n.getChild(0),parentAttr,env);
        if(expAttr.variableType != VariableType.Integer){
            errors.add("IntergerNode child type not Integer");
            return new AttributePanel(expAttr.stackSize, VariableType.Undecided);
        }else{
            return new AttributePanel(expAttr.stackSize, VariableType.Integer);
        }
    }

    private AttributePanel blockHandler(Node n, AttributePanel parentAttr, Environment env) {
        return null;
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
        return null;
    }

    private AttributePanel whileHandler(Node n, AttributePanel parentAttr, Environment env) {
        return null;
    }

    private AttributePanel outputHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel attr = parentAttr;
        for (int i = 0; i < n.childrenCount; i++) {
            attr = Evaluate(n.getChild(i),attr,env);
            generateCode("SOS OUTPUT",env);
            generateCode("SOS OUTPUTL", env);
            attr = new AttributePanel(attr.stackSize-1, attr.variableType);
        }
        return attr;
    }

    private AttributePanel readHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel attr = parentAttr;
        for (int i = 0; i < n.childrenCount; i++) {
            generateCode("SOS INPUT", env);
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
            generateCode("BOP BEQ", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("EQUAL values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel gteHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == rightAttr.variableType) {
            generateCode("BOP BGE", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Boolean);
        } else {
            errors.add("GTorEQ values cannot be different typed");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }


    //================== ARITHMETIC NODES =====================

    private AttributePanel modHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
            generateCode("BOP BMOD", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Integer);
        } else {
            errors.add("MOD non-integer values cannot be modulated");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }

    private AttributePanel minusHandler(Node n, AttributePanel parentAttr, Environment env) {
        AttributePanel leftAttr = Evaluate(n.getChild(0), parentAttr, env);
        AttributePanel rightAttr = Evaluate(n.getChild(1), leftAttr, env);
        if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
            generateCode("BOP BMINUS", env);
            return new AttributePanel(rightAttr.stackSize - 1, VariableType.Integer);
        } else {
            errors.add("MINUS non-integer values cannot be subtracted");
            return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
        }
    }


    //=================== LEAF NODES =======================

    private AttributePanel _integer_Handler(Node n, AttributePanel parentAttr, Environment env) {
        generateCode("LIT " + n.getChild(0).name, env);
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
