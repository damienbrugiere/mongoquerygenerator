package com.dbrugiere.mongoquerygenerator.composite;

import com.dbrugiere.mongoquerygenerator.exceptions.BuilderException;
import com.dbrugiere.mongoquerygenerator.logicOperators.AndLogicOperator;
import com.dbrugiere.mongoquerygenerator.logicOperators.LogicalOperator;
import com.dbrugiere.mongoquerygenerator.logicOperators.OrLogicOperator;
import com.dbrugiere.mongoquerygenerator.operators.EqOperator;
import com.dbrugiere.mongoquerygenerator.operators.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
    Collection<Composant> composants = recursiveMethod(this.query);
    if (composants == null) {
      throw new BuilderException();
    }
    if (composants.size() == 1) {
      return composants.stream().findFirst().get();
    }
    return new QueryComposite(composants, new AndLogicOperator());
  }

  /**
   * @param string
   * @return
   */
  private Collection<Composant> recursiveMethod(String string) {
    Collection<Composant> composants = new ArrayList<>();
    while(!string.isEmpty()){
      if (string.startsWith("&")) {
        string = string.substring(1);
      }
      if (string.endsWith("&")) {
        string = string.substring(0, string.length() - 1);
      }
      int andOperator = string.indexOf("and(");
      int orOperator = string.indexOf("or[");
      if (string.startsWith("and(")) {
        QueryComposite queryComposite = new QueryComposite();
        LogicalOperator operator = new AndLogicOperator();
        String queryContent = separeQueryByLogicalOperator(operator, string);
        queryComposite.setLogicalOperator(operator);
        queryComposite.addAllChildren(this.recursiveMethod(queryContent));
        composants.add(queryComposite);
        string = string.replace("and(" + queryContent + ")", "");
      } else if (string.startsWith("or[")) {
        QueryComposite queryComposite = new QueryComposite();
        LogicalOperator operator = new OrLogicOperator();
        String queryContent = separeQueryByLogicalOperator(operator, string);
        queryComposite.setLogicalOperator(operator);
        queryComposite.addAllChildren(this.recursiveMethod(queryContent));
        string = string.replace("or[" + queryContent + "]", "");
        composants.add(queryComposite);
      } else if (andOperator < 0 && orOperator < 0 ) {
         composants.addAll(buildComponentOfQuery(string));
         string = "";
      } else{
         Collection<String> values = new ArrayList<>();
         if((andOperator < 0 && orOperator>0) || orOperator<andOperator){
            String orSplit = string.substring(0,orOperator);
            composants.addAll(buildComponentOfQuery(orSplit));
            string = string.replace(orSplit,"");
         }else {
           String andSplit = string.substring(0,andOperator);
           composants.addAll(buildComponentOfQuery(andSplit));
           string = string.replace(andSplit,"");
         }
      }
    }

    return composants;
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

  private Collection<Composant> buildComponentOfQuery(String value) {
    if (value.startsWith("&")) {
      value = value.substring(1);
    }
    if (value.endsWith("&")) {
      value = value.substring(0, value.length() - 1);
    }
    if (value.contains("&")) {
      String[] componantQueries = value.split("&");
      Collection<Composant> queryComposites = Arrays.stream(componantQueries).map(iterator -> buildLeaf(iterator)).collect(Collectors.toList());
      return queryComposites;
    }
    if (value.split("&").length != 0) {
      return Arrays.asList(buildLeaf(value));
    }

    return null;
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
