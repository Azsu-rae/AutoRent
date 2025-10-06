package gui.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JTextField;

import orm.util.Reflection;

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

    public Object parse(String name, JTextField field) {
        try {
            return parser.get(reflect.fields.visibleTypeOf(name)).apply(field.getText());
        } catch (Exception e) {
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

    public String getMin(String att) {

        String start;
        if (reflect.fields.typeOf(att).equals(Double.class)) {
            start = "Min ";
        } else {
            start = "Start ";
        }

        return start + typeName.get(reflect.fields.typeOf(att)) + ":";
    }

    public String getMax(String att) {

        String end;
        if (reflect.fields.typeOf(att).equals(Double.class)) {
            end = "Max ";
        } else {
            end = "End ";
        }

        return end + typeName.get(reflect.fields.typeOf(att)) + ":";
    }

    public String formatName(String att) {
        if (reflect.fields.constraintsOf(att).lowerBound()) {
            return typeName.get(reflect.fields.typeOf(att)) + " Range";
        } else {
            return titleCase(att);
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
