package com.dbrugiere.mongoquerygenerator.logicOperators;

public abstract class LogicalOperator{
    protected String representation;


    protected LogicalOperator(String representation){
        this.representation = representation;
    }

    public abstract boolean isAndOperator();
    public abstract String getRepresentation();
}
