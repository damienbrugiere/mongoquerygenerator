package com.dbrugiere.mongoquerygenerator.composite;

import org.springframework.data.mongodb.core.query.CriteriaDefinition;

import java.util.Collection;
import java.util.List;

public interface Composant {

  CriteriaDefinition getQuery();

  List<String> getValueToMatchInTextCriteria(Collection<String> fields);
}
