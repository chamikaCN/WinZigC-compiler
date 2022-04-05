package main.java;

import java.util.HashMap;

public class Environment {
    static int counter = 2;
    HashMap<String, Integer> localLookupTable = null;
    HashMap<String, VariableType> localVariableTypes = null;
    String envName;
    int stackBottom;
    Environment functionParent = null;
    boolean isFunction;

    public Environment(int sb){
        envName = "L"+counter;
        counter++;
        functionParent = this;
        localLookupTable = new HashMap<>();
        localVariableTypes = new HashMap<>();
        stackBottom = sb;
    }

    public Environment(int sb, Environment funcPar){
        envName = "L"+counter;
        counter++;
        stackBottom = sb;
        functionParent = funcPar;
        if(functionParent != null) {
            localLookupTable = functionParent.localLookupTable;
            localVariableTypes = functionParent.localVariableTypes;
        }
    }


}
