package com.dbrugiere.mongoquerygenerator.logicOperators;

public class AndLogicOperator extends LogicalOperator {

    public AndLogicOperator(){
        super("and");
    }

    @Override
    public boolean isAndOperator() {
        return true;
    }

    @Override
    public String getRepresentation() {
        return this.representation;
    }
}
