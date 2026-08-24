package orm;

import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.BugDetectedException;
import util.Pair;

import orm.Table.Range;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static util.CaseConverter.*;

class SQLiteQueryConstructor {

    private final Table<?> instance;
    private final Reflection reflect;
    private final Model<?> model;

    final DataManipulation manipulate;

    SQLiteQueryConstructor(Table<?> instance) {

        this.instance = instance;
        this.reflect = instance.reflect;
        this.model = instance.model;

        this.manipulate = new DataManipulation();
    }

    class DataManipulation {

        static PreparedQuery select(
                Model<?> model,
                Vector<?> discreteCriterias,
                Vector<Range> boundedCriterias) {

            var queryString = new StringBuilder("SELECT * FROM " + DataDefinition.tableName(model));
            var queryInputs = new Vector<>();

            var enumerations = new ArrayList<String>();
            for (var field : model.record.getDeclaredFields()) {
                var values = new ArrayList<>();
                for (var criteria : discreteCriterias) {
                    Object value;
                    try {
                        value = field.get(criteria);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new BugDetectedException("Record fields are private?!?");
                    }
                    if (value == null) {
                        break;
                    }

                    values.add(value);
                }

                if (values.size() != 0) {
                    var joined = values.stream()
                            .map(_ -> "?")
                            .collect(Collectors.joining(", "));
                    enumerations.add(camelToSnake(field.getName()) + " IN(" + joined + ")");
                    queryInputs.addAll(values);
                }
            }

            if (enumerations.size() != 0) {
                var joined = String.join(" AND ", enumerations);
                queryString.append(" WHERE " + joined);

            }

            return new PreparedQuery(queryString.toString() + ";", queryInputs);
        }

        PreparedQuery insert() {

            var values = new Vector<>();
            var names = new ArrayList<String>();
            for (var column : model.columns) {
                Object curr = reflect.field(column.name).getValue();
                if (curr != null) {
                    values.add(curr);
                    names.add(camelToSnake(column.name));
                }
            }

            var queryString = "INSERT INTO "
                    + DataDefinition.tableName(model)
                    + "(" + String.join(", ", names)
                    + ") VALUES ("
                    + values.stream()
                            .map(_ -> "?")
                            .collect(Collectors.joining(", "))
                    + ");";

            return new PreparedQuery(queryString, values);
        }

        PreparedQuery update() {

            var names = new ArrayList<String>();
            var values = new Vector<>();
            for (var column : model.columns) {

                Object curr = reflect.field(column.name).getValue();
                if (curr == null) {
                    continue;
                }

                values.add(curr);
                names.add(camelToSnake(column.name));
            }

            var query = "UPDATE "
                    + DataDefinition.tableName(model)
                    + " SET "
                    + names.stream().map(name -> name + " = ?").collect(Collectors.joining(", "));

            query += " WHERE id=?;";
            values.add(instance.id);

            return new PreparedQuery(query.toString(), values);
        }
    }

    class DataDefinition {

        public static String tableName(Model<?> model) {
            return pascalToSnake(model.collectionName);
        }

        public static String tableDefinitionQuery(Model<?> model) {

            var tableName = tableName(model);

            StringBuilder table = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(");
            Vector<String> foreignKeyDefinitions = new Vector<>();
            Vector<String> columnDefinitions = new Vector<>();

            for (var column : model.columns) {

                var name = camelToSnake(column.name);
                if (column.constraints.foreignKey()) {
                    var inferedReferencedTable = pascalToSnake(column.type.getSimpleName());
                    foreignKeyDefinitions.add(
                            "FOREIGN KEY (" + name + ") REFERENCES " + inferedReferencedTable + "s(id)");
                }

                String columnDefinition = name + " " + column.constraints.type();
                columnDefinition += column.constraints.nullable() ? "" : " NOT NULL";
                columnDefinition += column.constraints.primaryKey() ? " PRIMARY KEY AUTOINCREMENT" : "";
                columnDefinitions.add(columnDefinition);
            }

            table.append(String.join(", ", columnDefinitions));
            if (foreignKeyDefinitions.size() != 0) {
                table.append(", " + String.join(", ", foreignKeyDefinitions));
            }

            table.append(");");

            return table.toString();
        }
    }

    class PreparedQuery extends Pair<String, Vector<Object>> {

        private PreparedQuery(String template, Vector<Object> values) {
            super(template, values);
        }

        String template() {
            return first;
        }

        Vector<Object> values() {
            return second;
        }
    }
}
