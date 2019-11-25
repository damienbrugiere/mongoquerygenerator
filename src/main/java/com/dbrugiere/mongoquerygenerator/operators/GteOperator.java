package com.dbrugiere.mongoquerygenerator.operators;

import com.dbrugiere.mongoquerygenerator.composite.QueryLeaf;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

public class GteOperator extends Operator{

    public GteOperator(){
        super("=>");
    }
    @Override
    public CriteriaDefinition generate(QueryLeaf queryLeaf) {
        return Criteria.where(queryLeaf.getProperty()).gte(queryLeaf.getValue());
    }
}
