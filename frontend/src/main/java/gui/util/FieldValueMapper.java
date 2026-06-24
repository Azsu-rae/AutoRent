package gui.util;

import java.time.LocalDate;

import java.util.*;
import java.util.function.*;

import orm.Table;

import static orm.Reflection.fieldsOf;

public class FieldValueMapper {

    private static Map<Class<?>, Function<String, Object>> parser = new HashMap<>();
    static {
        parser.put(Integer.class, Integer::parseInt);
        parser.put(Double.class, Double::parseDouble);
        parser.put(String.class, s -> s.equals("") ? null : s);
        parser.put(LocalDate.class, s -> orm.Table.stringToDate(s));
    }

    static public Object[] getModifiablesAsObjects(Table tuple) {
        return tuple.reflect.fields.modifiable()
                .stream()
                .map(attribute -> (Object) tuple.reflect.fields.get(attribute))
                .toArray(Object[]::new);
    }

    static public Object parse(String ORMModelName, String attributeName, String valueAsString) {
        return parser
                .get(fieldsOf(ORMModelName).typeOf(attributeName))
                .apply(valueAsString);
    }

    static public Object[] getAsRow(Table tuple) {
        return Arrays
                .asList(tuple.reflect.fields.names)
                .stream()
                .map(attribute -> getAsColumn(tuple, attribute))
                .toArray(Object[]::new);
    }

    static public Object getAsColumn(orm.Table tuple, String name) {
        Object value = tuple.reflect.fields.get(name);
        if (value instanceof orm.Table) {
            value = ((orm.Table) value).getId();
        }
        return value;
    }

}
