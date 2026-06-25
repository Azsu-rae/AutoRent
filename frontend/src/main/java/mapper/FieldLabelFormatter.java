package gui.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static orm.Reflection.fieldsOf;

public class FieldLabelFormatter {

    static Map<Class<?>, String> typeName = new HashMap<>();
    static {
        typeName.put(Integer.class, "Year");
        typeName.put(Double.class, "Amount");
        typeName.put(LocalDate.class, "Date");
    }

    static public String[] titleCaseNames(String[] attNames) {
        return Arrays
                .asList(attNames)
                .stream()
                .map(att -> titleCase(att))
                .toArray(String[]::new);
    }

    static public String titleCase(String attName) {
        attName = attName.substring(0, 1).toUpperCase() + attName.substring(1);
        if (orm.Table.hasSubClass(attName)) {
            return attName + " ID";
        } else if (attName.equals("Id")) {
            return "ID";
        } else {
            return attName.replaceAll("([a-z])([A-Z])", "$1 $2");
        }
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

}
