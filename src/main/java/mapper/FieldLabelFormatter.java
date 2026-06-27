package mapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import orm.Reflection;
import util.Pair;

import static orm.Reflection.fieldsOf;

public class FieldLabelFormatter {

    static Map<Class<?>, String> typeName = new HashMap<>();
    static {
        typeName.put(Integer.class, "Year");
        typeName.put(Double.class, "Amount");
        typeName.put(LocalDate.class, "Date");
    }

    private String ORMModelName;

    public FieldLabelFormatter(String ORMModelName) {
        this.ORMModelName = ORMModelName;
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

    public String formatAttNameForFiltering(String attribute) {
        var fields = fieldsOf(ORMModelName);
        var constraints = fields.constraintsOf(attribute);
        if (constraints.lowerBound() || constraints.bounded()) {
            return typeName.get(fields.typeOf(attribute)) + " Range";
        } else {
            return titleCase(attribute);
        }
    }

    public String getMin(String attribute) {

        var fields = fieldsOf(ORMModelName);
        String start;

        if (fields.typeOf(attribute).equals(Double.class)) {
            start = "Min ";
        } else {
            start = "Start ";
        }

        return start + typeName.get(fields.typeOf(attribute)) + ":";
    }

    public String getMax(String attribute) {

        var fields = fieldsOf(ORMModelName);
        String end;

        if (fields.typeOf(attribute).equals(Double.class)) {
            end = "Max ";
        } else {
            end = "End ";
        }

        return end + typeName.get(fields.typeOf(attribute)) + ":";
    }

    public class RangeLabel extends Pair<String, String> {
        public RangeLabel(String boundedAttribute) {
            var constraints = Reflection.fieldsOf(ORMModelName).constraintsOf(boundedAttribute);
            if (constraints.lowerBound()) {
                first = titleCase(boundedAttribute) + ":";
                second = titleCase(constraints.boundedPair()) + ":";
            } else if (constraints.bounded()) {
                first = getMin(boundedAttribute);
                second = getMax(boundedAttribute);
            }
        }

        public String start() {
            return first;
        }

        public String end() {
            return second;
        }
    }
}
