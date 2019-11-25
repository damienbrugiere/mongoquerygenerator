import com.dbrugiere.mongoquerygenerator.composite.QueryBuilder;
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

    @Test
    public void shouldBuildComplexeQuery(){
        CriteriaDefinition criteria = new QueryBuilder("and(or[t=12&tr=ggg]&tyyyty=1&dgdgd=2)").build().getQuery();
        Assertions.assertThat(criteria.getCriteriaObject().toJson()).isEqualTo("{ \"$and\" : [{ \"$or\" : [{ \"$and\" : [{ \"t\" : \"12\" }, { \"tr\" : \"ggg\" }] }], { \"tyyyty\" : \"12\" }, { \"dgdgd\" : \"ggg\" } }] }");
    }

}