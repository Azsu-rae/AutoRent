package util;

import static orm.util.Console.print;

import java.time.LocalDate;
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

    Reflection reflect;

    public Parser(Reflection reflect) {
        this.reflect = reflect;
    }

    static public Object[] getModifiablesAsRow(Table tuple) {
        return tuple.reflect.fields.modifiable().stream()
                .map(attribute -> (Object) tuple.reflect.fields.get(attribute)).toArray(Object[]::new);
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
