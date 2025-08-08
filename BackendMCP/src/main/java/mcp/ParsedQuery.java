package mcp;

import orm.Table;
import orm.model.*;

import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import java.time.LocalDate;

import java.lang.reflect.*;

import static utilities.ReflectionUtils.*;

@SuppressWarnings("rawtypes")
public class ParsedQuery {

    private String operation;
    private String modelType;
    private Map<String,Vector<Object>> attributes;

    private ParsedQuery(String operation, String modelType) {

        this.operation = operation;
        this.modelType = modelType;
        this.attributes = new HashMap<>();
    }

    public static ParsedQuery create(String operation, String modelType) {

        if (operation == null || modelType == null) {
            return null;
        }

        if (!Arrays.asList("search", "create", "update", "delete").contains(operation)) {
            return null;
        }

        return new ParsedQuery(operation, modelType);
    }

    public String execute() {

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
                return "Unsupported operation.";
        }
    }

    public void setAtrribute(String name, Object value) {

    }

    private String search() {

        return "";
    }

    private String create() {

        return "";
    }

    private String delete() {

        return "";
    }

    private String update() {
        return "";
    }
}
