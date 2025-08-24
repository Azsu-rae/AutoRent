package mcp;

import java.util.Vector;

import orm.Table;
import orm.util.Pair;
import orm.util.Reflection;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

class ParsedQuery {

    final private String operation;
    final private String model;

    final private Reflection reflect;
    final private Map<String,Vector<Object>> discreteAttributes;
    final private Vector<Pair<Object,Object>> boundedCriterias;

    private ParsedQuery(String operation, String model) {

        this.operation = operation;
        this.model = model;

        this.reflect = new Reflection(model);
        this.discreteAttributes = new HashMap<>();
        this.boundedCriterias = new Vector<>();
    }

    public static ParsedQuery create(String operation, String model) {

        boolean nil = operation == null || model == null;
        boolean op = Arrays.asList("search", "create", "update", "delete").contains(operation);
        boolean mdl = Table.hasSubClass(model);

        if (!nil && op && mdl) {
            return new ParsedQuery(operation, model);
        } else {
            String s = "Invalid operation: %s or model: %s";
            throw new IllegalArgumentException(String.format(s, operation, model));
        }
    }

    public <T> T execute() {

        return switch (operation) {
            case "search"   -> execute(Vector.class);
            default         -> execute(Boolean.class);
        }
    }

    public <T> T execute(Class<T> type) {

        switch (operation) {
            case "search":
                return search();
            case "create":
                return create();
            case "update":
                return update();
            case "delete":
                return delete();
            default:
                return null;
        }
    }

    public void setCriteria(String name, Pair<Object,Object> value) {

        if (!value.isValidCriteriaFor(reflect)) {
            String s = "Invalid attribute : %s for model: %s";
            throw new IllegalArgumentException(String.format(s, value.toString(), model));
        }

        boundedCriterias.add(value);
    }

    public void setCriteria(String name, Object value) {

        if (!reflect.getDiscreteFieldNames().contains(name)) {
            String s = "Invalid attribute name: %s for model: %s";
            throw new IllegalArgumentException(String.format(s, name, model));
        }

        if (!reflect.getFieldType(name).equals(value.getClass())) {
            String s = "Invalid type: %s for attribute: %s of model:  %s";
            throw new IllegalArgumentException(String.format(s, value.getClass().getSimpleName(), name, model));
        }

        discreteAttributes.computeIfAbsent(name, k -> new Vector<>()).add(value);
    }

    private Vector<Table> search() {

        Vector<Table> discreteCriterias = new Vector<>();
        for (Map.Entry<String,Vector<Object>> entry : discreteAttributes.entrySet()) {

            int count = 1;
            for (Object att : entry.getValue()) {

                if (count > discreteCriterias.size()) {
                    discreteCriterias.add(Reflection.getModelInstance(model));
                }

                discreteCriterias.elementAt(count-1).reflect.setAttribute(entry.getKey(), att);
                count++;
            }
        }

        return Table.search(discreteCriterias, boundedCriterias);
    }

    private Vector<Table> create() {

        return null;
    }

    private Vector<Table> delete() {

        return null;
    }

    private Vector<Table> update() {

        return null;
    }
}
