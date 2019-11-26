package com.dbrugiere.mongoquerygenerator.composite;

import com.dbrugiere.mongoquerygenerator.logicOperators.LogicalOperator;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QueryComposite implements Composant {

    private Collection<Composant> children;
    private LogicalOperator logicalOperator;

    public QueryComposite(Collection<Composant> children,
                          LogicalOperator logicalOperator) {
        this.children = children;
        this.logicalOperator = logicalOperator;
    }
    public QueryComposite(){
        this(new ArrayList<>(),null);
    }

    public void addChildren(Composant composant){
        if(composant != null){
            this.children.add(composant);
        }
    }

    public void addChildren(Composant... composants){
        if(composants != null){
            this.children.addAll(Arrays.asList(composants));
        }
    }

    public void addAllChildren(Collection<Composant> composants){
        if(composants != null){
            this.children.addAll(composants);
        }
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    @Override
    public CriteriaDefinition getQuery() {
        if (logicalOperator == null || logicalOperator.isAndOperator()) {
            return new Criteria().andOperator(this.children.stream().map(iterator -> iterator.getQuery()).toArray(size -> new Criteria[size]));
        } else {
            return new Criteria().orOperator(this.children.stream().map(iterator -> iterator.getQuery()).toArray(size -> new Criteria[size]));
        }
    }

    @Override
    public List<String> getValueToMatchInTextCriteria(Collection<String> field) {
        return null;
    }
}
