package mcp;

import static orm.Reflection.getModelInstance;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import orm.Table;
import orm.util.Pair;

public class ParsedQuery {

    private String[] operations = {"search", "add", "edit", "delete"};
    private String operation;
    private String modelName;

    public class Args {

        Vector<Pair<Object,Object>> bounedCriterias;
        Map<String,List<Object>> attributes;
        Vector<Table> tuples;

        public Result areValid() {

            switch (operation) {
                case "search":
                    return forSearch();
                case "add":
                    return forAdd();
                case "edit":
                    return forEdit();
                case "delete":
                    return forDelete();
                default:
                    return null;
            }
        }

        private Result forEdit() {

            boolean valid = true;

            for (Table tuple : tuples) {
                valid = valid && Table.isTuple(tuple);
            }

            if (!valid) {
                return new Result("Cannot have an invalid tuple while attempting an edit!", false, null);
            }

            for (String key : attributes.keySet()) {
                valid = valid && (attributes.get(key).size() <= 1);
            }

            if (!valid) {
                return new Result("One value per attribute when editting!", false, null);
            }

            return new Result(null, true, null);
        }

        private Result forAdd() {
            return null;
        }

        private Result forDelete() {
            return null;
        }

        private Result forSearch() {
            return null;
        }
    }

    public class Result extends Pair<String,Boolean> {

        Vector<Table> set;

        public Result(String message, Boolean success, Vector<Table> set) {
            super(message, success);
            this.set = set;
        }
    }
}
