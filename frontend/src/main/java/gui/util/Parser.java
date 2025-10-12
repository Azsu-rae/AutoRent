package gui.util;

import static orm.Reflection.fieldsOf;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JTextField;

import orm.Reflection;

public class Parser {

    static Map<Class<?>,Function<String,Object>> parser = new HashMap<>();
    static {
        parser.put(Integer.class, Integer::parseInt);
        parser.put(Double.class,  Double::parseDouble);
        parser.put(String.class, s -> s.equals("") ? null : s);
        parser.put(LocalDate.class,  s -> orm.Table.stringToDate(s));
    }

    static Map<Class<?>,String> typeName = new HashMap<>();
    static {
        typeName.put(Integer.class, "Year");
        typeName.put(Double.class,  "Amount");
        typeName.put(LocalDate.class,  "Date");
    }

    Reflection reflect;
    public Parser(Reflection reflect) {
        this.reflect = reflect; 
    }

    static public String[] titleCaseNames(String[] attNames) {
        var titleCaseNames = new String[attNames.length];

        int i=0;
        for (var name : attNames) {
            titleCaseNames[i++] = titleCase(name);
        } return titleCaseNames;
    }

    static public Object parse(Attribute attribute) {
        try {
            return parser
                .get(fieldsOf(attribute.model()).typeOf(attribute.name()))
                .apply((String)attribute.value());
        } catch (Exception e) {
            System.out.println(String.format("we tried %s", attribute.name()));
            e.printStackTrace();
            return null;
        }
    }

    static public Object[] getAsRow(orm.Table tuple) {
        Object[] values = new Object[tuple.reflect.fields.count];
        int i=0;
        for (String att : tuple.reflect.fields.names) {
            values[i++] = getAsColumn(tuple, att);
        } return values;
    }

    static public Object getAsColumn(orm.Table tuple, String name) {
        Object value = tuple.reflect.fields.get(name);
        if (value instanceof orm.Table) {
            value = ((orm.Table) value).getId();
        } return value;
    }

    static public String titleCase(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        if (orm.Table.hasSubClass(name)) {
            return name + " ID";
        } else if (name.equals("Id")) {
            return "ID";
        } else {
            return name.replaceAll("([a-z])([A-Z])", "$1 $2");
        }
    }

    static public String getMin(Attribute attribute) {

        var fields = fieldsOf(attribute.model());
        String start;

        if (fields.typeOf(attribute.name()).equals(Double.class)) {
            start = "Min ";
        } else {
            start = "Start ";
        }

        return start + typeName.get(fields.typeOf(attribute.name())) + ":";
    }

    static public String getMax(Attribute attribute) {

        var fields = fieldsOf(attribute.model());
        String end;

        if (fields.typeOf(attribute.name()).equals(Double.class)) {
            end = "Max ";
        } else {
            end = "End ";
        }

        return end + typeName.get(fields.typeOf(attribute.name())) + ":";
    }

    static public String formatName(Attribute attribute) {
        var fields = fieldsOf(attribute.model());
        var constraints = fields.constraintsOf(attribute.name());
        if (constraints.lowerBound() || constraints.bounded()) {
            return typeName.get(fields.typeOf(attribute.name())) + " Range";
        } else {
            return titleCase(attribute.name());
        }
    }

    public String denominator(String[] atts) {
        for (String att : atts) {
            if (!att.contains("name")) {
                return "";
            }
        } return " by Name";
    }
}
