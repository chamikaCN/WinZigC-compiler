package main.java;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeGenerator {

    private Node rootNode;
    private ArrayList<String> code = new ArrayList<>();
    private ArrayList<String> errors = new ArrayList<>();
    private HashMap<String, Integer> globalLookupTable = new HashMap<>();
    private HashMap<String, VariableType> globalVariableTypes = new HashMap<>();
    private HashMap<String, Environment> environments = new HashMap<>();

    void initialize(Node r) {
        rootNode = r;
        Evaluate(r,new AttributePanel(0),null);
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

    private AttributePanel Evaluate(Node n, AttributePanel attr, Environment currentEnvironment) {
        switch (n.name) {
            case "<integer>":
                code.add("LIT " + n.left.name);
                return new AttributePanel(attr.stackSize + 1, VariableType.Integer);
            case "<identifier>":
                String line = generateLoadIdentifierLine(n.left.name, currentEnvironment);
                VariableType idType = retrieveVariableType(n.left.name, currentEnvironment);
                if (line.equals("ERROR")) {
                    errors.add("<identifier> " + n.left.name + " is undefined");
                } else if (idType == VariableType.Undecided) {
                    errors.add("<identifier> " + n.left.name + " type not defined");
                } else {
                    code.add(line);
                    return new AttributePanel(attr.stackSize + 1, idType);
                }
                return new AttributePanel(attr.stackSize, VariableType.Undecided);
            case "-":
                AttributePanel leftAttr = Evaluate(n.left, attr, currentEnvironment);
                AttributePanel rightAttr = Evaluate(n.right, leftAttr, currentEnvironment);
                if (leftAttr.variableType == VariableType.Integer && rightAttr.variableType == VariableType.Integer) {
                    code.add("BOP BMINUS");
                    return new AttributePanel(rightAttr.stackSize - 2, VariableType.Integer);
                } else {
                    errors.add("MINUS non-integer values cannot be subtracted");
                    return new AttributePanel(rightAttr.stackSize, VariableType.Undecided);
                }
            case "assign":
                int identifierIndex = retrieveIdentifierIndex(n.left.left.name, currentEnvironment);
                AttributePanel expAttr = Evaluate(n.right, attr, currentEnvironment);
                if (identifierIndex < 0) {
                    errors.add("ASSIGN <identifier> " + n.left.left.name + " is undefined");
                    return new AttributePanel(expAttr.stackSize - 1, VariableType.Undecided);
                } else {
                    code.add(generateReassignIdentifierLine(n.left.left.name, currentEnvironment));
                    return new AttributePanel(expAttr.stackSize, VariableType.Undecided);
                }
            case "var":
                idType = getVariableTypeFromString(n.right.left.name);
                if (idType == VariableType.Undecided) {
                    errors.add("<identifier> " + n.left.left.name + " type not valid");
                } else {
                    line = generateDefineIdentifierLine(n.left.left.name, currentEnvironment, attr.stackSize, idType);
                    if (line.equals("ERROR")) {
                        errors.add("<identifier> " + n.left.left.name + " is already defined in the scope");
                    } else {
                        code.add(line);
                        return new AttributePanel(attr.stackSize + 1, idType);
                    }
                    return new AttributePanel(attr.stackSize, VariableType.Undecided);
                }
            case "integer":
            case "output":
            case "mod":
            case "=":
            case "if":
            case ">=":
            case "while":
            case "read":
            case "block":
            case "subprogs":
            case "dclns":
            case "types":
            case "consts":
            case "program":
            default:
                return new AttributePanel(attr.stackSize, VariableType.Integer);
        }

    }

}
