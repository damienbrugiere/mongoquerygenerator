package com.dbrugiere.mongoquerygenerator.composite;

import com.dbrugiere.mongoquerygenerator.operators.Operator;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QueryLeaf implements Composant {
    private String property;
    private Operator operator;
    private String value;

    public QueryLeaf(String property,
                     Operator operator,
                     String value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public CriteriaDefinition getQuery() {
        return operator.generate(this);
    }

    @Override
    public List<String> getValueToMatchInTextCriteria(Collection<String> fields) {
        if(fields.contains(this.property)){
            return Arrays.asList(this.getValue().split(","));
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getProperty() {
        return property;
    }
}
