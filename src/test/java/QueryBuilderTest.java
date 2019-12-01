import com.dbrugiere.mongoquerygenerator.composite.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

public class QueryBuilderTest {

    @Test
    public void shouldCreateSimpleCriteria(){
        CriteriaDefinition criteria = new QueryBuilder("field1=test").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"field1\" : \"test\" }");
    }

    @Test
    public void shouldCreateDoubleCriteria(){
        CriteriaDefinition criteria = new QueryBuilder("field1=test&field2=tutu").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"$and\" : [{ \"field1\" : \"test\" }, { \"field2\" : \"tutu\" }] }");
    }


    @Test
    public void shouldCreateTripleCriteria(){
        CriteriaDefinition criteria = new QueryBuilder("field1=test&field2=tutu&field3=tyty").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"$and\" : [{ \"field1\" : \"test\" }, { \"field2\" : \"tutu\" }, { \"field3\" : \"tyty\" }] }");
    }

    @Test
    public void shouldCreateTripleCriteriaWithAnd(){
        CriteriaDefinition criteria = new QueryBuilder("and(field1=test&field2=tutu&field3=tyty)").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"$and\" : [{ \"field1\" : \"test\" }, { \"field2\" : \"tutu\" }, { \"field3\" : \"tyty\" }] }");
    }

    @Test
    public void shouldCreateTripleCriteriaWithOr(){
        CriteriaDefinition criteria = new QueryBuilder("or[field1=test&field2=tutu&field3=tyty]").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"$or\" : [{ \"field1\" : \"test\" }, { \"field2\" : \"tutu\" }, { \"field3\" : \"tyty\" }] }");
    }
// a corriger pas de and dans le or
    @Test
    public void shouldBuildComplexeQuery(){
        CriteriaDefinition criteria = new QueryBuilder("and(or[t=12&tr=ggg]&tyyyty=1&dgdgd=2)").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"$and\" : [{ \"$or\" : [{ \"t\" : \"12\" }, { \"tr\" : \"ggg\" }] }, { \"tyyyty\" : \"1\" }, { \"dgdgd\" : \"2\" }] }");
    }

    @Test
    public void shouldBuildComplexeQuery2(){
        CriteriaDefinition criteria = new QueryBuilder("tyyyty=1&dgdgd=2&or[and(t=12&tr=ggg)and(ty=5&te=8)]").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"$and\" : [{ \"tyyyty\" : \"1\" }, { \"dgdgd\" : \"2\" }, { \"$or\" : [{ \"$and\" : [{ \"t\" : \"12\" }, { \"tr\" : \"ggg\" }] }, { \"$and\" : [{ \"ty\" : \"5\" }, { \"te\" : \"8\" }] }] }] }");
    }

    @Test
    public void shouldTest(){
        String s = "and(or(t=12&tr=ggg)&tyyyty=1&dgdgd=2)";
        String[] sub = StringUtils.substringsBetween(s,"and(",")");
        System.out.println(sub);
        Assertions.assertThat(sub).isEqualTo("or[t=12&tr=ggg]&tyyyty=1&dgdgd=2");
    }

}