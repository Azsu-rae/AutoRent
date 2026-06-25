package mapper;

import java.time.LocalDate;

import java.util.*;
import java.util.function.*;

import orm.Table;
import orm.Table.Range;

import static orm.Reflection.fieldsOf;

public class FieldValueMapper {

    private static Map<Class<?>, Function<String, Object>> formParser = new HashMap<>();
    static {
        formParser.put(Integer.class, Integer::parseInt);
        formParser.put(Double.class, Double::parseDouble);
        formParser.put(String.class, s -> s.equals("") ? null : s);
        formParser.put(LocalDate.class, s -> orm.Table.stringToDate(s));
    }

    private String ORMModelName;

    public FieldValueMapper(String ORMModelName) {
        this.ORMModelName = ORMModelName;
    }

    static public Object[] getModifiablesAsObjects(Table tuple) {
        return tuple.reflect.fields.modifiable()
                .stream()
                .map(attribute -> (Object) tuple.reflect.fields.get(attribute))
                .toArray(Object[]::new);
    }

    static public Object parse(String ORMModelName, String attributeName, String valueAsString) {
        return formParser
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

    public class RangeParser {
        String attributeName;

        public RangeParser(String boundedAttribute) {
            this.attributeName = boundedAttribute;
        }

        public Range parse(String lower, String upper) {
            return new Range(
                    attributeName,
                    FieldValueMapper.parse(ORMModelName, attributeName, lower),
                    FieldValueMapper.parse(ORMModelName, attributeName, upper));
        }
    }
}
