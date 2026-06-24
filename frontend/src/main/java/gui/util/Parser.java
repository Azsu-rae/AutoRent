package gui.util;

import static orm.util.Console.print;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import orm.Reflection;
import orm.Table;

public class Parser {

    static Map<Class<?>, Function<String, Object>> parser = new HashMap<>();
    static {
        parser.put(Integer.class, Integer::parseInt);
        parser.put(Double.class, Double::parseDouble);
        parser.put(String.class, s -> s.equals("") ? null : s);
        parser.put(LocalDate.class, s -> {
            print("Trying to parse %s", s);
            return orm.Table.stringToDate(s);
        });
    }

    static Map<Class<?>, String> typeName = new HashMap<>();
    static {
        typeName.put(Integer.class, "Year");
        typeName.put(Double.class, "Amount");
        typeName.put(LocalDate.class, "Date");
    }

    Reflection reflect;

    public Parser(Reflection reflect) {
        this.reflect = reflect;
    }

    static public Object[] getModifiablesAsRow(Table tuple) {
        return tuple.reflect.fields.modifiable().stream()
                .map(attribute -> (Object) tuple.reflect.fields.get(attribute)).toArray(Object[]::new);
    }

    static public Object getAsColumn(orm.Table tuple, String name) {
        Object value = tuple.reflect.fields.get(name);
        if (value instanceof orm.Table) {
            value = ((orm.Table) value).getId();
        }
        return value;
    }

    static public String getMin(Attribute<Object> attribute) {

        var fields = fieldsOf(attribute.ORMModelName);
        String start;

        if (fields.typeOf(attribute.name).equals(Double.class)) {
            start = "Min ";
        } else {
            start = "Start ";
        }

        return start + typeName.get(fields.typeOf(attribute.name)) + ":";
    }

    static public String getMax(Attribute<Object> attribute) {

        var fields = fieldsOf(attribute.ORMModelName);
        String end;

        if (fields.typeOf(attribute.name).equals(Double.class)) {
            end = "Max ";
        } else {
            end = "End ";
        }

        return end + typeName.get(fields.typeOf(attribute.name)) + ":";
    }

    static public <T> String formatName(Attribute<T> attribute) {
        var fields = fieldsOf(attribute.ORMModelName);
        var constraints = fields.constraintsOf(attribute.name);
        if (constraints.lowerBound() || constraints.bounded()) {
            return typeName.get(fields.typeOf(attribute.name)) + " Range";
        } else {
            return titleCase(attribute.name);
        }
    }

    public String denominator(String[] atts) {
        for (String att : atts) {
            if (!att.contains("name")) {
                return "";
            }
        }
        return " by Name";
    }
}
