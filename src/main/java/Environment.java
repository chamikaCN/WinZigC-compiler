package main.java;

import java.util.HashMap;

public class Environment {
    HashMap<String, Integer> localLookupTable = null;
    HashMap<String, VariableType> localVariableTypes = null;
    String envName;
    int stackBottom;
    Environment functionParent = null;
    boolean isFunction;

    public Environment(int sb, int envNumber){
        envName = "L"+envNumber;
        functionParent = this;
        localLookupTable = new HashMap<>();
        localVariableTypes = new HashMap<>();
        stackBottom = sb;
    }

    public Environment(int sb, int envNumber, Environment funcPar){
        envName = "L"+envNumber;
        stackBottom = sb;
        functionParent = funcPar;
        if(functionParent != null) {
            localLookupTable = functionParent.localLookupTable;
            localVariableTypes = functionParent.localVariableTypes;
        }
    }


}
