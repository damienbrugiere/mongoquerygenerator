package com.dbrugiere.mongoquerygenerator.operators;

import com.dbrugiere.mongoquerygenerator.composite.QueryLeaf;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

public abstract class Operator {
    public static String representation;


    public Operator(String representation){
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public abstract  CriteriaDefinition generate(QueryLeaf queryLeaf);
}
