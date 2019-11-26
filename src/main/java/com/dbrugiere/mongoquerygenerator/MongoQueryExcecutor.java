package com.dbrugiere.mongoquerygenerator;

import com.dbrugiere.mongoquerygenerator.composite.Composant;
import com.dbrugiere.mongoquerygenerator.composite.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class MongoQueryExcecutor<T> {

    @Autowired
    private MongoTemplate template;

    public Page<T> executeQuery(String query, Pageable pageable, boolean caseSensitive, Class<T> entityClass,  Collection<String> fieldsWithTextIndexed) {
        Assert.isTrue(!StringUtils.isEmpty(query), "The query is empty or null");
        Assert.isTrue(CollectionUtils.isEmpty(fieldsWithTextIndexed), "The fieldsWithTextIndexed is empty or null");
        Assert.isTrue(pageable != null, "The pageable is null");
        Composant composant = new QueryBuilder(query).build();
        CriteriaDefinition criteriaDefinition = composant.getQuery();
        String[] valuesTextIndexed = composant.getValueToMatchInTextCriteria(fieldsWithTextIndexed).stream().filter(Objects::nonNull).toArray(size -> new String[size]);
        Query executeQuery = TextQuery.queryText(TextCriteria.forDefaultLanguage().caseSensitive(caseSensitive).matchingAny(valuesTextIndexed)).sortByScore().addCriteria(criteriaDefinition).with(pageable);
        List<T> content = template.find(executeQuery, entityClass);
        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> template.count(executeQuery, entityClass));
    }

    public List<T> executeQuery(String query, boolean caseSensitive, Class<T> entityClass,  Collection<String> fieldsWithTextIndexed) {
        Assert.isTrue(!StringUtils.isEmpty(query), "The query is empty or null");
        Assert.isTrue(CollectionUtils.isEmpty(fieldsWithTextIndexed), "The fieldsWithTextIndexed is empty or null");
        Composant composant = new QueryBuilder(query).build();
        CriteriaDefinition criteriaDefinition = composant.getQuery();
        String[] valuesTextIndexed = composant.getValueToMatchInTextCriteria(fieldsWithTextIndexed).stream().filter(Objects::nonNull).toArray(size -> new String[size]);
        Query executeQuery = TextQuery.queryText(TextCriteria.forDefaultLanguage().caseSensitive(caseSensitive).matchingAny(valuesTextIndexed)).sortByScore().addCriteria(criteriaDefinition);
        return template.find(executeQuery, entityClass);
    }

    public List<T> executeQuery(String query, boolean caseSensitive, Class<T> entityClass) {
        Assert.isTrue(!StringUtils.isEmpty(query), "The query is empty or null");
        Composant composant = new QueryBuilder(query).build();
        CriteriaDefinition criteriaDefinition = composant.getQuery();
         Query executeQuery = new Query(criteriaDefinition);
        return template.find(executeQuery, entityClass);
    }

}
