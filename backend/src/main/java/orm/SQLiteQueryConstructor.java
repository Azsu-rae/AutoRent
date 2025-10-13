package orm;

import java.util.Arrays;
import java.util.Vector;

import orm.util.Constraints;
import orm.util.Pair;
import static orm.util.Console.print;

import orm.Table.Range;

class SQLiteQueryConstructor {

    final Table instance;
    final Vector<Column> columns;

    final String tableName;
    final DataDefinition define;
    final DataManipulation manipulate;

    SQLiteQueryConstructor(Table instance) {

        this.instance = instance;
        this.tableName = instance.getClass().getSimpleName().toLowerCase() + "s";

        this.columns = new Vector<>();

        this.define = new DataDefinition();
        this.manipulate = new DataManipulation();
    }

    class DataManipulation {

        StringBuilder queryString;
        Vector<Object> queryInputs;

        int checkedBoundedCriterias, currentAttribute, i;
        boolean where, close;
        Column col;

        private DataManipulation() {}

        void init(String s) {

            queryString = new StringBuilder(s);
            queryInputs = new Vector<>();
            checkedBoundedCriterias = 0;
            currentAttribute = 0;
            where = true;
            close = false;
        }

        PreparedQuery select(Vector<? extends Table> discreteCriterias, Vector<Range> boundedCriterias) {

            init("SELECT * FROM " + tableName);

            for (i=0;i<columns.size();i++) {

                col = columns.elementAt(i);

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

            init("INSERT INTO " + tableName + "(");
            StringBuilder valuesQuery = new StringBuilder("VALUES (");

            boolean first = true;
            for (i=1;i<columns.size();i++) {

                Object curr = instance.reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                queryString.append((first ? "" : ", ") + columns.elementAt(i).name());
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

            StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
            Vector<Object> inputs = new Vector<>();

            boolean first = true;
            for (int i=1;i<columns.size();i++) {

                Object curr = instance.reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                query.append((!first ? ", " : "") + columns.elementAt(i).name() + " = ? "); 
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

                if (!criteria.isValidCriteriaFor(instance.reflect)) {
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

            for (int j=0;j<discreteCriterias.size();j++) {

                Object curr = discreteCriterias.elementAt(j).reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                if (appendConnector(", ?", curr)) {
                    continue;
                }

                if (columns.elementAt(i).constraints().searchedText()) {
                    boolean needOr = false;
                    for (var att : instance.reflect.fields.haveConstraint(Constraints::searchedText)) {
                        queryString.append((needOr ? " OR " : "") + att);
                        queryString.append(" LIKE ?");
                        queryInputs.add(String.valueOf(curr)+"%");
                        needOr = true;
                    } continue;
                }

                queryString.append(columns.elementAt(i).name());
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
                } return true;
            } else if (currentAttribute < i) {
                queryString.append((close ? ")" : "" ) + " AND ");
                close = false;
            }
            currentAttribute = i;
            return false;
        }

        private void appendOverlap(String lowerBoundName, String upperBoundName, Object lowerBound, Object upperBound) {

            String overlapCondition =
                "(" + lowerBoundName + " BETWEEN ? AND ?) OR " +
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

        final private String tableCreationQuery;

        private DataDefinition() {

            StringBuilder table = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(");
            String[] names = Arrays.asList(instance.reflect.fields.names).toArray(String[]::new);
            Constraints[] constraints = instance.reflect.fields.constraints;
            Vector<String> foreignKeys = new Vector<>();
            boolean first = true;

            for (int i=0;i<instance.reflect.fields.count;i++) {

                if (constraints[i].foreignKey()) {
                    String foreignKey = "FOREIGN KEY (id_%s) REFERENCES %ss(id)";
                    foreignKeys.add(String.format(foreignKey, names[i], names[i]));
                    names[i] = "id_" + names[i];
                }

                table
                    .append(first ? "" : ", ")
                    .append(names[i] + " " + constraints[i].type())
                    .append(constraints[i].nullable() ? "" : " NOT NULL")
                    .append(constraints[i].primaryKey() ? " PRIMARY KEY AUTOINCREMENT" : "");

                columns.add(new Column(names[i], constraints[i]));
                first = false;
            }

            for (String fk : foreignKeys) {
                table.append(", " + fk);
            }
            table.append(");");

            this.tableCreationQuery = table.toString();
        }

        String table() {
            return tableCreationQuery;
        }
    }

    class Column extends Pair<String,Constraints> {

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

    class PreparedQuery extends Pair<String,Vector<Object>> {

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
