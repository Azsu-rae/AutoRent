package orm;

import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Stream;

import util.CaseConverter;
import util.Pair;

import orm.Table.Range;
import orm.annotation.Constraints;

import static util.Console.print;
import static util.CaseConverter.*;

class SQLiteQueryConstructor {

    final Table instance;
    final Reflection reflect;

    final DataDefinition define;
    final DataManipulation manipulate;

    SQLiteQueryConstructor(Table instance) {

        this.instance = instance;
        this.reflect = instance.reflect;

        this.define = new DataDefinition();
        this.manipulate = new DataManipulation();
    }

    class DataManipulation {

        StringBuilder queryString;
        Vector<Object> queryInputs;

        int checkedBoundedCriterias, currentAttribute, i;
        boolean where, close;
        Column col;

        private DataManipulation() {
        }

        void init(String s) {

            queryString = new StringBuilder(s);
            queryInputs = new Vector<>();
            checkedBoundedCriterias = 0;
            currentAttribute = 0;
            where = true;
            close = false;
        }

        PreparedQuery select(Vector<? extends Table> discreteCriterias, Vector<Range> boundedCriterias) {

            init("SELECT * FROM " + define.tableName);

            for (i = 0; i < define.columnInfos.size(); i++) {

                col = define.columnInfos.elementAt(i);

                if (col.constraints().upperBound()) {
                    continue;
                }

                if (col.constraints().bounded() || col.constraints().lowerBound()) {
                    appendBoundedCondition(boundedCriterias);
                } else {
                    appendDiscreteCondition(discreteCriterias);
                }
            }
            queryString.append((close ? ")" : ""));

            return new PreparedQuery(queryString.toString() + ";", queryInputs);
        }

        PreparedQuery insert() {

            init("INSERT INTO " + define.tableName + "(");
            StringBuilder valuesQuery = new StringBuilder("VALUES (");

            boolean first = true;
            for (i = 1; i < define.columnInfos.size(); i++) {

                Object curr = reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                queryString.append((first ? "" : ", ") + define.columnInfos.elementAt(i).name());
                valuesQuery.append((first ? "" : ", ") + "?");
                queryInputs.add(curr);
                first = false;
            }

            queryString.append(") ");
            valuesQuery.append(");");
            String pstmt = queryString.toString() + valuesQuery.toString();

            return new PreparedQuery(pstmt, queryInputs);
        }

        PreparedQuery update() {

            StringBuilder query = new StringBuilder("UPDATE " + define.tableName + " SET ");
            Vector<Object> inputs = new Vector<>();

            boolean first = true;
            for (int i = 1; i < define.columnInfos.size(); i++) {

                Object curr = reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                query.append((!first ? ", " : "") + define.columnInfos.elementAt(i).name() + " = ? ");
                inputs.add(curr);
                first = false;
            }
            query.append("WHERE id=?;");
            inputs.add(instance.id);

            return new PreparedQuery(query.toString(), inputs);
        }

        private void appendBoundedCondition(Vector<Range> boundedCriterias) {

            if (boundedCriterias == null || checkedBoundedCriterias == boundedCriterias.size()) {
                return;
            }

            for (Range criteria : boundedCriterias) {

                if (!criteria.isValidCriteriaFor(reflect)) {
                    String s = "Invalid bounded criteria: %s!";
                    throw new IllegalArgumentException(String.format(s, criteria));
                }

                if (!criteria.attributeName.equals(col.name())) {
                    continue;
                }

                appendConnector(" OR ");

                Object lowerBound = criteria.lowerBound(), upperBound = criteria.upperBound();
                if (col.constraints().lowerBound()) {
                    appendOverlap(col.name(), col.constraints().boundedPair(), lowerBound, upperBound);
                } else {
                    queryString.append(col.name() + " BETWEEN ? AND ?");
                    queryInputs.add(lowerBound);
                    queryInputs.add(upperBound);
                }

                checkedBoundedCriterias++;
            }
        }

        private void appendDiscreteCondition(Vector<? extends Table> discreteCriterias) {

            for (int j = 0; j < discreteCriterias.size(); j++) {

                Object curr = discreteCriterias.elementAt(j).reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                if (appendConnector(", ?", curr)) {
                    continue;
                }

                if (define.columnInfos.elementAt(i).constraints().searchedText()) {
                    boolean needOr = false;
                    for (var att : reflect.fields.haveConstraint(Constraints::searchedText)) {
                        queryString.append((needOr ? " OR " : "") + att);
                        queryString.append(" LIKE ?");
                        queryInputs.add(String.valueOf(curr) + "%");
                        needOr = true;
                    }
                    continue;
                }

                queryString.append(define.columnInfos.elementAt(i).name());
                queryString.append(" IN (?");
                queryInputs.add(curr);
                close = true;
            }
        }

        private void appendConnector(String connector) {
            appendConnector(connector, null);
        }

        private boolean appendConnector(String connector, Object curr) {

            if (where) {
                queryString.append(" WHERE ");
                where = false;
            } else if (currentAttribute == i) {
                queryString.append(connector);
                if (curr != null) {
                    queryInputs.add(curr);
                }
                return true;
            } else if (currentAttribute < i) {
                queryString.append((close ? ")" : "") + " AND ");
                close = false;
            }
            currentAttribute = i;
            return false;
        }

        private void appendOverlap(String lowerBoundName, String upperBoundName, Object lowerBound, Object upperBound) {

            String overlapCondition = "(" + lowerBoundName + " BETWEEN ? AND ?) OR " +
                    "(" + upperBoundName + " BETWEEN ? AND ?) OR " +
                    "(" + lowerBoundName + " < ? AND " + upperBoundName + " > ?)";

            queryString.append("(" + overlapCondition.toString() + ")");
            queryInputs.add(lowerBound);
            queryInputs.add(upperBound);
            queryInputs.add(lowerBound);
            queryInputs.add(upperBound);
            queryInputs.add(lowerBound);
            queryInputs.add(upperBound);
        }
    }

    class DataDefinition {

        final String tableName;
        final Vector<Column> columnInfos;

        final private String tableCreationQuery;

        /*
         * CREATE TABLE payments(
         * id INTEGER PRIMARY KEY AUTOINCREMENT,
         * id_reservation INTEGER NOT NULL,
         * date DATE NOT NULL,
         * method TEXT NOT NULL,
         * amount DECIMAL NOT NULL,
         * FOREIGN KEY (id_reservation) REFERENCES reservations(id)
         * )
         */
        private DataDefinition() {

            tableName = pascalToSnake(instance.getClass().getSimpleName()) + "s";
            columnInfos = new Vector<>();

            Constraints[] constraints = reflect.fields.constraints;
            String[] names = camelToSnake(reflect.fields.names);
            String[] types = pascalToSnake(Stream
                    .of(reflect.fields.types)
                    .map(type -> type.getClass().getSimpleName())
                    .toArray(String[]::new));

            StringBuilder table = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(");
            Vector<String> foreignKeyDefinitions = new Vector<>();
            Vector<String> columnDefinitions = new Vector<>();

            for (int i = 0; i < reflect.fields.count; i++) {

                if (constraints[i].foreignKey()) {
                    names[i] = names[i] + "_id";
                    foreignKeyDefinitions.add("FOREIGN KEY (" + names[i] + ") REFERENCES " + types[i] + "s(id)");
                }

                String columnDefinition = names[i] + " " + constraints[i].type();
                columnDefinition += constraints[i].nullable() ? "" : " NOT NULL";
                columnDefinition += constraints[i].primaryKey() ? " PRIMARY KEY AUTOINCREMENT" : "";
                columnDefinitions.add(columnDefinition);

                columnInfos.add(new Column(names[i], constraints[i]));
            }

            String columns = String.join(", ", columnDefinitions);
            String foreignKeys = String.join(", ", foreignKeyDefinitions);

            if (!foreignKeys.equals("")) {
                table.append(String.join(", ", columns, foreignKeys));
            } else {
                table.append(columns);
            }

            table.append(");");

            this.tableCreationQuery = table.toString();
        }

        String table() {
            return tableCreationQuery;
        }
    }

    class Column extends Pair<String, Constraints> {

        private Column(String name, Constraints constraints) {
            super(name, constraints);
        }

        String name() {
            return first;
        }

        Constraints constraints() {
            return second;
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
