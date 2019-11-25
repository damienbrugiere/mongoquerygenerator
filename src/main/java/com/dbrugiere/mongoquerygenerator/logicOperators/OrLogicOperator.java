package com.dbrugiere.mongoquerygenerator.logicOperators;

public class OrLogicOperator extends LogicalOperator {

    public OrLogicOperator(){
        super("or");
    }

    @Override
    public boolean isAndOperator() {
        return false;
    }
    @Override
    public String getRepresentation() {
        return this.representation;
    }
}
