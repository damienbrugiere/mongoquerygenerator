package com.dbrugiere.mongoquerygenerator.composite;

import com.dbrugiere.mongoquerygenerator.exceptions.BuilderException;
import com.dbrugiere.mongoquerygenerator.logicOperators.AndLogicOperator;
import com.dbrugiere.mongoquerygenerator.logicOperators.LogicalOperator;
import com.dbrugiere.mongoquerygenerator.logicOperators.OrLogicOperator;
import com.dbrugiere.mongoquerygenerator.operators.EqOperator;
import com.dbrugiere.mongoquerygenerator.operators.Operator;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryBuilder {

    private String query;

    public QueryBuilder(String query) {
        this.query = query;
    }

    /**
     * si a and ou or on va dans la métode récursive une fois qu'on a a plus au build la feuille et on remonte.
     *
     * @return
     */
    public Composant build() {
        return recursiveMethod(this.query);
    }

    /**
     * @param string
     * @return
     */
    private Composant recursiveMethod(String string) {
        QueryComposite queryComposite = new QueryComposite();
        if (string.startsWith("and(")) {
            LogicalOperator operator = new AndLogicOperator();
            String queryContent = separeQueryByLogicalOperator(operator, string);
            String regex = "and\\(" + queryContent + "\\)";
            string = string.replaceFirst(regex, "");
            if (string.isEmpty()) {
                return this.buildComponentOfQuery(queryContent, operator);
            }
            queryComposite.addChildren(recursiveMethod(queryContent));
        } else if (string.startsWith("or[")) {
            LogicalOperator operator = new OrLogicOperator();
            String queryContent = separeQueryByLogicalOperator(operator, string);
            queryComposite.setLogicalOperator(operator);
            String regex = "or\\[" + queryContent + "\\]";
            string = string.replaceFirst(regex, "");
            if (string.isEmpty()) {
                return this.buildComponentOfQuery(queryContent, operator);
            }
            queryComposite.addChildren(recursiveMethod(queryContent));
        } else if (!string.startsWith("and(") && !string.startsWith("or[") && !string.contains("and(") && !string.contains("or[")) {
            return this.buildComponentOfQuery(string, new AndLogicOperator());
        }
        return queryComposite;
    }

    private QueryLeaf buildLeaf(String value) {
        if (value.contains("=")) {
            String[] values = value.split("=");
            if (values.length == 2) {
                return new QueryLeaf(values[0], new EqOperator(), values[1]);
            }
        }
        return null;
    }


    private Composant buildComponentOfQuery(String value, LogicalOperator operator) {
        if (value.startsWith("&")) {
            value = value.substring(1);
        }
        if (value.endsWith("&")) {
            value = value.substring(0, value.length() - 1);
        }
        if (value.contains("&")) {
            String[] componantQueries = value.split("&");
            Collection<Composant> queryComposites = Arrays.stream(componantQueries).map(iterator -> buildLeaf(iterator)).collect(Collectors.toList());
            return new QueryComposite(queryComposites, operator);
        }
        return buildLeaf(value);
    }


    private String separeQueryByLogicalOperator(LogicalOperator operator, String string) {
        String regex = null;
        if (operator.isAndOperator()) {
            regex = operator.getRepresentation() + "\\((.+?)\\)";
        } else {
            regex = operator.getRepresentation() + "\\[(.+?)\\]";
        }
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        String newQuery = matcher.group(1);
        if (StringUtils.isEmpty(newQuery)) {
            throw new BuilderException();
        }
        return newQuery;
    }
}
