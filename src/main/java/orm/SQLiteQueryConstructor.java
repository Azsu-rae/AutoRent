package orm;

import java.util.ArrayList;
import java.util.List;
import util.Pair;

import orm.Table.Range;
import orm.reflect.Meta;
import orm.reflect.Model;
import orm.reflect.Reflected;

import static util.CaseConverter.*;
import static java.util.stream.Collectors.joining;

import static util.Log.*;

class SQLiteQueryConstructor {

    private final Table<?> instance;
    private final Model<?> model;

    final DataManipulation manipulate;

    private final String tableName;

    SQLiteQueryConstructor(Table<?> instance) {
        this.instance = instance;
        this.model = instance.model;

        this.tableName = DataDefinition.tableName(model);
        this.manipulate = new DataManipulation();
    }

    class DataManipulation {

        static <V> PreparedQuery select(Meta<?,V> meta, List<? extends Reflected<?,V>> discreteCriterias, List<Range> boundedCriterias) {

            var queryString = new StringBuilder("SELECT * FROM " + DataDefinition.tableName(meta.model()));
            var queryInputs = new ArrayList<>(); // corresponding Object to each '?'

            // an 'enumeration' is a String of the form: <field> IN(<value1>, <value2>, <value3>, ...)
            var enumerations = new ArrayList<String>();
            for (var component : meta.components()) {

                // getting all the values `component` takes
                var values = new ArrayList<>();
                for (var criteria : discreteCriterias) {
                    var reflected = criteria;
                    Object value = reflected.reflect(component).getValue();
                    if (value == null) {
                        break;
                    } values.add(value);
                }

                // constructing the enumeration
                if (values.size() != 0) {
                    queryInputs.addAll(values);
                    enumerations.add(meta.nameOf(component)
                            + " IN(" + values.stream().map(_ -> "?").collect(joining(", ")) + ")");
                }
            }

            if (enumerations.size() != 0) {
                queryString.append(" WHERE " + String.join(" AND ", enumerations));

            }

            return new PreparedQuery(queryString.toString() + ";", queryInputs);
        }

        PreparedQuery insert() {

            var columnNames = new ArrayList<String>();
            var values = new ArrayList<>();

            // getting all the (columName, value) couples
            for (var column : model.columns) {
                Object curr = instance.reflect(column.field).getValue();
                if (curr != null) {
                    columnNames.add(column.sqlName());
                    values.add(curr);
                }
            }

            var queryString = "INSERT INTO " + tableName + "(" + String.join(", ", columnNames) + ") VALUES ("
                    + values.stream()
                            .map(_ -> "?")
                            .collect(joining(", "))
                    + ");";

            return new PreparedQuery(queryString, values);
        }

        PreparedQuery update() {

            var columnNames = new ArrayList<String>();
            var values = new ArrayList<>();

            // getting all the (columnName, value) couples
            for (var column : model.columns) {
                Object curr = instance.reflect(column.field).getValue();
                if (curr == null) {
                    values.add(curr);
                    columnNames.add(column.sqlName());
                }
            }

            var query = "UPDATE " + tableName + " SET "
                + columnNames
                    .stream()
                    .map(name -> name + " = ?")
                    .collect(joining(", "))
                + " WHERE id=?;";
            values.add(instance.id);

            return new PreparedQuery(query, values);
        }
    }

    class DataDefinition {

        public static String tableName(Model<?> model) {
            return camelToSnake(model.collectionName);
        }

        public static String tableDefinitionQuery(Model<?> model) {

            var tableName = tableName(model);

            List<String> foreignKeyDefinitions = new ArrayList<>();
            List<String> columnDefinitions = new ArrayList<>();

            for (var column : model.columns) {

                var name = column.sqlName();
                if (column.constraints.foreignKey()) {
                    var referencedTable = tableName(Model.ofWildcard(column.type));
                    foreignKeyDefinitions.add("FOREIGN KEY (" + name + ") REFERENCES " + referencedTable + "(id)");
                }

                String columnDefinition = name + " " + column.constraints.type();
                columnDefinition += column.constraints.nullable() ? "" : " NOT NULL";
                columnDefinitions.add(columnDefinition);
            }

            StringBuilder table = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(id INTEGER PRIMARY KEY AUTOINCREMENT, ");

            table.append(String.join(", ", columnDefinitions));
            if (foreignKeyDefinitions.size() != 0) {
                table.append(", " + String.join(", ", foreignKeyDefinitions));
            }

            table.append(");");

            return table.toString();
        }
    }

    static class PreparedQuery extends Pair<String, List<Object>> {

        private PreparedQuery(String template, List<Object> values) {
            super(template, values);
        }

        String template() {
            return first;
        }

        List<Object> values() {
            return second;
        }
    }
}
