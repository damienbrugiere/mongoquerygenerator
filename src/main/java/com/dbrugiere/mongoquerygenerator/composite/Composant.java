package com.dbrugiere.mongoquerygenerator.composite;

import org.springframework.data.mongodb.core.query.CriteriaDefinition;

public interface Composant {

    CriteriaDefinition getQuery();
}
