package com.dbrugiere.mongoquerygenerator;

import com.dbrugiere.mongoquerygenerator.annotations.QueryObject;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldUtils {

    public static Map<String, Field> getAllFields(Class<? extends  Object> clazz, final String property) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        Reflections reflections = new Reflections("com.dbrugiere.mongoquerygenerator");
        Map<String, Field> map1 = new HashMap<>();
//        for (Class<? extends Object> iterator : reflections.getSubTypesOf(clazz)) {
//            Map<String, Field> map2 = FieldUtils.getAllFields(iterator, property);
//            map1 = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        }
        Set<Class<?>> classes = FieldUtils.getAllExtendedOrImplementedTypesRecursively(clazz);
        for (Class<?> iterator : classes) {
            Map<String, Field> map2 = FieldUtils.getAllFields(iterator, property);
            map1 = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        final String propertyBuffer = FieldUtils.concatProperty(property);
        for (Field field : fields) {
            if ("this$0".equals(field.getName())) {
                continue;
            }
            map1.put(propertyBuffer + field.getName(), field);
            if (field.getType().isAnnotationPresent(QueryObject.class)) {
                Map<String, Field> map2 = FieldUtils.getAllFields(field.getType(), propertyBuffer + field.getName());
                map1 = Stream.concat(map1.entrySet().stream(), map2.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
        }
        return map1;
    }

    private static String concatProperty(String property) {
        if (property == null) {
            return "";
        }
        return property + ".";
    }

    private static Set<Class<?>> getAllExtendedOrImplementedTypesRecursively(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();
        clazz = clazz.getSuperclass();
        while (!"java.lang.Object".equals(clazz.getCanonicalName())) {
            res.add(clazz);

            // First, add all the interfaces implemented by this class
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                res.addAll(Arrays.asList(interfaces));

                for (Class<?> interfaze : interfaces) {
                    res.addAll(getAllExtendedOrImplementedTypesRecursively(interfaze));
                }
            }

            // Add the super class
            Class<?> superClass = clazz.getSuperclass();

            // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
            if (superClass == null) {
                break;
            }

            // Now inspect the superclass
            clazz = superClass;
        }

        return new HashSet<Class<?>>(res);
    }
}
