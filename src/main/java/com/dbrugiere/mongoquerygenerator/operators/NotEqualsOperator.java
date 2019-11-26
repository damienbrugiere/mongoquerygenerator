package com.dbrugiere.mongoquerygenerator.operators;

import com.dbrugiere.mongoquerygenerator.composite.QueryLeaf;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

public class NotEqualsOperator extends Operator {

    public NotEqualsOperator(){
        super("!=");
    }

    @Override
    public CriteriaDefinition generate(QueryLeaf queryLeaf) {
        return Criteria.where(queryLeaf.getProperty()).ne(queryLeaf.getValue());
    }
}
