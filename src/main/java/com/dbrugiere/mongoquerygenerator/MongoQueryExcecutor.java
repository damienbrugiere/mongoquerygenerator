package com.dbrugiere.mongoquerygenerator;

import com.dbrugiere.mongoquerygenerator.composite.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoQueryExcecutor<T> {

    @Autowired
    private MongoTemplate template;

    public Page<T> executeQuery(String query, Pageable pageable, boolean caseSensitive, Class<T> entityClass) {
        Query executeQuery = TextQuery.queryText(TextCriteria.forDefaultLanguage().caseSensitive(caseSensitive).matchingAny("", "")).sortByScore().addCriteria(new QueryBuilder(query).build().getQuery()).with(pageable);
        List<T> content = template.find(executeQuery, entityClass);
        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> template.count(executeQuery, entityClass));
    }
}
