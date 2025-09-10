package orm;

import java.util.Arrays;
import java.util.Vector;

import orm.util.Constraints;
import orm.util.Pair;

import static orm.util.Utils.format;

class SQLiteQueryConstructor {

    final private Table instance;
    final private Vector<Column> columns;

    final String tableName;
    final public DataDefinition define;
    final public DataManipulation manipulate;

    SQLiteQueryConstructor(Table instance) {

        this.instance = instance;
        this.tableName = instance.getClass().getSimpleName().toLowerCase() + "s";

        this.columns = new Vector<>();

        this.define = new DataDefinition();
        this.manipulate = new DataManipulation();
    }

    class Column {

        String name;
        Constraints constraints;

        private Column(String name, Constraints constraints) {
            this.name = name;
            this.constraints = constraints;
        }
    }

    Column getColumn(int i) {
        return columns.elementAt(i);
    }

    class DataManipulation {

        StringBuilder queryString;
        Vector<Object> queryInputs;

        int checkedBoundedCriterias, currentAttribute, i;
        boolean where, close;

        String colName;
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

        Pair<String,Vector<Object>> select(
            Vector<? extends Table> discreteCriterias,
            Vector<Pair<Object,Object>> boundedCriterias) {

            init("SELECT * FROM " + tableName);

            for (i=0;i<columns.size();i++) {

                Column col = getColumn(i);

                if (col.constraints.upperBound()) {
                    continue;
                }

                if (col.constraints.bounded() && col.constraints.lowerBound()) {
                    appendBoundedCondition(boundedCriterias);
                } else {
                    appendDiscreteCondition(discreteCriterias);
                }
            }
            queryString.append((close ? ")" : ""));

            return new Pair<>(queryString.toString() + ";", queryInputs);
        }

        Pair<String,Vector<Object>> insert() {

            init("INSERT INTO " + tableName + "(");
            StringBuilder valuesQuery = new StringBuilder("VALUES (");

            boolean first = true;
            for (i=1;i<columns.size();i++) {

                Object curr = instance.reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                queryString.append((first ? "" : ", ") + getColumn(i).name);
                valuesQuery.append((first ? "" : ", ") + "?");
                queryInputs.add(curr);
                first = false;
            }

            queryString.append(") ");
            valuesQuery.append(");");
            String pstmt = queryString.toString() + valuesQuery.toString();

            return new Pair<>(pstmt, queryInputs);
        }

        Pair<String,Vector<Object>> update() {

            StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
            Vector<Object> inputs = new Vector<>();

            boolean first = true;
            for (int i=1;i<columns.size();i++) {

                Object curr = instance.reflect.fields.get(i);
                if (curr == null) {
                    continue;
                }

                query.append((!first ? ", " : "") + getColumn(i).name + " = ? "); 
                inputs.add(curr);
                first = false;
            }
            query.append("WHERE id=?;");
            inputs.add(instance.id);

            return new Pair<>(query.toString(), inputs);
        }

        private void appendBoundedCondition(Vector<Pair<Object,Object>> boundedCriterias) {

            if (boundedCriterias == null || checkedBoundedCriterias == boundedCriterias.size()) {
                return;
            }

            for (Pair<Object,Object> criteria : boundedCriterias) {

                if (!criteria.isValidCriteriaFor(instance.reflect)) {
                    String s = "Invalid bounded criteria: %s!";
                    throw new IllegalArgumentException(format(s, criteria));
                }

                if (!criteria.attributeName.equals(colName)) {
                    continue;
                }

                appendConnector(" OR ");

                Object lowerBound = criteria.first, upperBound = criteria.second;
                if (col.constraints.lowerBound()) {
                    appendOverlap(colName, lowerBound, upperBound);
                } else {
                    queryString.append(colName + " BETWEEN ? AND ?");
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

                queryString.append(getColumn(i).name);
                if (getColumn(i).constraints.searchedText()) {
                    queryString.append(" LIKE ?");
                    queryInputs.add(String.valueOf(curr)+"%");
                } else {
                    queryString.append(" IN (?");
                    queryInputs.add(curr);
                    close = true;
                }
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

        private String getUpperBoundName() {

            String upperBoundName = "";
            for (int j=i+1;j<columns.size();j++) {
                if (getColumn(j).constraints.upperBound()) {
                    upperBoundName = getColumn(j).name;
                    break;
                }
            }

            if (upperBoundName.equals("")) {
                String s = "Couldn't find a name for the upper bound in class %s!";
                throw new IllegalStateException(format(s, instance.getClass().getSimpleName() + "!"));
            }

            return upperBoundName;
        }

        private void appendOverlap(String attName, Object lowerBound, Object upperBound) {

            String lowerBoundName = attName, upperBoundName = getUpperBoundName();

            String overlapCondition = 
                "(" + lowerBoundName + " BETWEEN ? AND ?) OR " +
                "(" + upperBoundName + "BETWEEN ? AND ?) OR " +
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
}
