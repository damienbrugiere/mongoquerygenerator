package com.dbrugiere.mongoquerygenerator;

import com.dbrugiere.mongoquerygenerator.annotations.QueryObject;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FieldUtilsTest {

    @QueryObject
    public class SimpleObject {
        private String field1;
        private String field2;
    }

    @QueryObject
    public class SimpleObjectWithNestedObject {
        private String field1;
        private SimpleObject field3;
    }

    @QueryObject
    public class ComplexObject extends SimpleObject {
        private String field4;
    }

    @QueryObject
    public class OtherObject {
        private String field;
        private ComplexObject complexObject;
    }

    @QueryObject
    public class Mother {
        private String field;
    }

    @QueryObject
    public class Children1 extends Mother {
        private String field2;
    }

    @QueryObject
    public class Children2 extends Mother {
        private String field1;
    }

    @QueryObject
    public class ComponentObject {
        private Mother mother;
    }

    @Test
    public void shouldGetAllField() throws NoSuchFieldException {
        Map<String, Field> result = new HashMap<>();
        result.put("field1", SimpleObject.class.getDeclaredField("field1"));
        result.put("field2", SimpleObject.class.getDeclaredField("field2"));
        Assertions.assertThat(FieldUtils.getAllFields(SimpleObject.class, null)).isEqualTo(result);
    }

    @Test
    public void shouldGetAllFieldFromComplexObjectOther() throws NoSuchFieldException {
        Map<String, Field> result = new HashMap<>();
        result.put("field", OtherObject.class.getDeclaredField("field"));
        result.put("complexObject", OtherObject.class.getDeclaredField("complexObject"));
        result.put("complexObject.field1", SimpleObject.class.getDeclaredField("field1"));
        result.put("complexObject.field2", SimpleObject.class.getDeclaredField("field2"));
        result.put("complexObject.field4", ComplexObject.class.getDeclaredField("field4"));
        Assertions.assertThat(FieldUtils.getAllFields(OtherObject.class, null)).isEqualTo(result);
    }

    @Test
    public void shouldGetAllFieldFromComplexObject() throws NoSuchFieldException {
        Map<String, Field> result = new HashMap<>();
        result.put("field1", SimpleObject.class.getDeclaredField("field1"));
        result.put("field2", SimpleObject.class.getDeclaredField("field2"));
        result.put("field4", ComplexObject.class.getDeclaredField("field4"));
        Assertions.assertThat(FieldUtils.getAllFields(ComplexObject.class, null)).isEqualTo(result);
    }

    @Test
    public void shouldGetAllFieldWithNestedObject() throws NoSuchFieldException {
        Map<String, Field> result = new HashMap<>();
        result.put("field1", SimpleObjectWithNestedObject.class.getDeclaredField("field1"));
        result.put("field3", SimpleObjectWithNestedObject.class.getDeclaredField("field3"));
        result.put("field3.field1", SimpleObject.class.getDeclaredField("field1"));
        result.put("field3.field2", SimpleObject.class.getDeclaredField("field2"));
        Assertions.assertThat(FieldUtils.getAllFields(SimpleObjectWithNestedObject.class, null)).isEqualTo(result);
    }

    @Ignore
    @Test
    public void shouldGetAllMotherAndChildrenFields() throws NoSuchFieldException {
        Map<String, Field> result = new HashMap<>();
        result.put("mother", ComponentObject.class.getDeclaredField("mother"));
        result.put("mother.field", Mother.class.getDeclaredField("field"));
        result.put("mother.field1", Children2.class.getDeclaredField("field1"));
        result.put("mother.field2", Children1.class.getDeclaredField("field2"));
        Assertions.assertThat(FieldUtils.getAllFields(ComponentObject.class, null)).isEqualTo(result);
    }
}