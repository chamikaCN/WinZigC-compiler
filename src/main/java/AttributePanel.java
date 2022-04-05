package main.java;


public class AttributePanel {
    int stackSize;
    VariableType variableType = null;

    public AttributePanel(int size){
        stackSize = size;
    }

    public AttributePanel(int size, VariableType type){
        stackSize = size;
        variableType = type;
    }

}
