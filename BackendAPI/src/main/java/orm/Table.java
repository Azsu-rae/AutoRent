package orm;

import utilities.Pair;
import utilities.Column;
import utilities.ReflectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.io.File;

import java.util.Arrays;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public abstract class Table {

    public static Info info;
    static {

        info = new Info();

        info.addModel("Client", "Name", "Surname", "Email", "PhoneNumber", "DrivingLicence");
        info.addModel("Vehicule", "PricePerDay", "State", "MaintenanceDate", "Year", "Brand", "Model", "VehiculeType", "FuelType");
        info.addModel("Reservation", "Client", "Vehicule", "StartDate", "EndDate", "Status");
        info.addModel("Return", "Reservation", "ReturnDate", "ReturnState", "AdditionalFees");
        info.addModel("Payment", "Reservation", "Amount", "Date", "Method");
        info.addModel("User", "Name", "Surname", "Email", "Password", "Role");

        info.addType(
            String.class, 
            ResultSet::getString,
            (pstmt, i, value) -> pstmt.setString(i, (String) value)
        );
        info.addType(
            Double.class, 
            ResultSet::getDouble,
            (pstmt, i, value) -> pstmt.setDouble(i, (Double) value)
        );
        info.addType(
            Integer.class, 
            ResultSet::getInt,
            (pstmt, i, value) -> pstmt.setInt(i, (Integer) value)
        );
        info.addType(
            LocalDate.class, 
            (rs, col) -> stringToDate(rs.getString(col)),
            (pstmt, i, value) -> pstmt.setString(i, value.toString())
        );
    }

    @Column(type = "INTEGER", primaryKey = true)
    protected Integer id;

    final private Vector<Pair<String,Column>> attributes;
    final private String tableCreationQuery;
    final private Integer attributesNumber;
    final private ReflectionUtils reflect;
    final private String sqliteTableName;

    protected Table() {

        this.reflect = new ReflectionUtils(this);
        this.sqliteTableName = this.getClass().getSimpleName().toLowerCase() + "s";
        this.attributesNumber = reflect.getAttributesNumber();
        this.attributes = new Vector<>();

        StringBuilder table = new StringBuilder("CREATE TABLE IF NOT EXISTS " + sqliteTableName + "(");
        String[] fieldNames = reflect.getFieldNames();
        Column[] columns = reflect.getFieldColumns();
        Vector<String> foreignKeys = new Vector<>();
        boolean first = true;

        for (int i=0;i<attributesNumber;i++) {

            if (columns[i].foreignKey()) {
                foreignKeys.add("FOREIGN KEY (id_" + fieldNames[i] + ") REFERENCES " + fieldNames[i] + "s(id)");
                fieldNames[i] = "id_" + fieldNames[i];
            }

            table
                .append(first ? "" : ", ")
                .append(fieldNames[i] + " " + columns[i].type())
                .append(columns[i].nullable() ? "" : " NOT NULL")
                .append(columns[i].primaryKey() ? " PRIMARY KEY AUTOINCREMENT" : "");

            attributes.add(new Pair<>(fieldNames[i], columns[i]));
            first = false;
        }

        for (String fk : foreignKeys) {
            table.append(", " + fk);
        }
        table.append(");");

        this.tableCreationQuery = table.toString();
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder(". " + this.getClass().getSimpleName() + "\n|\n+->");

        int n = attributesNumber;
        boolean first = true;
        for (int i=1;i<n;i++) {

            Object curr = reflect.getAttribute(i);
            if (curr == null) {
                continue;
            }

            if (Arrays.asList(info.subClasses).contains(curr.getClass().getSimpleName())) {
                boolean firstLine = true;
                for (String line : curr.toString().split("\n")) {
                    s.append((firstLine ? "" : "|  ") + line + "\n");
                    firstLine = false;
                }
                s.append("|\n+->");
            } else {
                    s.append((first ? " Attributes: (" : ", " ) + curr.toString());
                    first = false;
            }
        }
        s.append(first ? " EMPTY" : ")");
        if (id != null) {
            s.append("\n+-> ID: " + id);
        }

        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (this.id == null) {
            return false;
        }

        return this.id.equals( ((Table)obj).getId());
    }

    public static Vector<Table> search(String className) {

        return search(ReflectionUtils.getModelInstance(className));
    }

    public static Vector<Table> search(Table discreteCriteria) {

        return search(discreteCriteria, null, null, null);
    }

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias) {

        return search(discreteCriterias, null);
    }

    public static Vector<Table> search(Table discreteCriteria, String boundedAttributeName, Object lowerBound, Object upperBound) {

        if (discreteCriteria == null) {
            throw new IllegalArgumentException("Give a discrete criteria when searching!");
        }

        Vector<Table> discreteContainer = new Vector<>();
        discreteContainer.add(discreteCriteria);

        Vector<Pair<Object,Object>> boundedContainer = null;
        if (boundedAttributeName != null && lowerBound != null && upperBound != null) {
            boundedContainer = new Vector<>();
            Pair<Object,Object> range = new Pair<>(lowerBound, upperBound);
            range.attributeName = boundedAttributeName;
            boundedContainer.add(range);
        } 

        return search(discreteContainer, boundedContainer);
    }

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias, Vector<Pair<Object,Object>> boundedCriterias) {

        if (discreteCriterias == null || discreteCriterias.size() == 0) {
            throw new IllegalArgumentException("Give a discrete criteria when searching!");
        }

        Table modelData = discreteCriterias.elementAt(0);
        String sqliteTableName = modelData.sqliteTableName;

        if (!db(sqliteTableName)) {
            throw new IllegalStateException("No Database or no table found!");
        }

        Pair<String,Vector<Object>> scratched = scratch(discreteCriterias, boundedCriterias);
        Vector<Table> tuples = null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            setValues(pstmt, scratched.second);
            tuples = getTuples(pstmt, modelData.getClass().getSimpleName());

        } catch (SQLException e) {
            System.err.println(scratched.first);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tuples;
    }

    public static boolean add(Table tuple) {

        if (tuple == null || !tuple.isValid()) {
            return false;
        }

        Pair<String,Vector<Object>> scratched = scratch(tuple);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
             Statement stmt = conn.createStatement();) {

            stmt.execute(tuple.tableCreationQuery);

            PreparedStatement pstmt = conn.prepareStatement(scratched.first);
            setValues(pstmt, scratched.second);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("tableCreationQuery: " + tuple.tableCreationQuery + "\nQuery: " + scratched.first);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean edit() {

        if (!isValid() || !db(sqliteTableName) || id == null) {
            return false;
        }

        Pair<String,Vector<Object>> scratched = scratch();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            setValues(pstmt, scratched.second);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(scratched.first);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean delete() {

        if (!db(sqliteTableName) || id == null) {
            return false;
        }

        String sql = "DELETE FROM " + sqliteTableName + " WHERE id=?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(sql);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        reflect.cascadeDeletion();
        return true;
    }

    public Integer getId() {

        return this.id;
    }

    public boolean isValid() {

        boolean valid = true;
        for (int i=1;i<attributesNumber;i++) {
            Column col = attributes.elementAt(i).second;
            if (!col.nullable() && reflect.getAttribute(i) == null) {
                valid = false;
                break;
            }
        }

        return valid;
    }

    private static Pair<String,Vector<Object>> scratch(Vector<? extends Table> discreteCriterias, Vector<Pair<Object,Object>> boundedCriterias) {

        Table modelData = discreteCriterias.elementAt(0);

        StringBuilder query = new StringBuilder("SELECT * FROM " + modelData.sqliteTableName);
        Vector<Object> inputs = new Vector<>();

        int currentAttribute = 0, checkedBoundedCriterias = 0;
        boolean close = false, where = true;

        for (int i=0;i<modelData.attributesNumber;i++) {

            Pair<String,Column> attribute = modelData.attributes.elementAt(i);
            if (attribute.second.upperBound()) {
                continue;
            }

            if (attribute.second.bounded() || attribute.second.lowerBound()) {

                if (boundedCriterias == null || checkedBoundedCriterias == boundedCriterias.size()) {
                    continue;
                }

                for (Pair<Object,Object> criteria : boundedCriterias) {

                    if (!criteria.isValid() || Arrays.asList(modelData.reflect.getBoundedFieldNames()).contains(criteria.attributeName)) {
                        throw new IllegalArgumentException("Invalid boundedCriteria!");
                    }

                    if (criteria.attributeName.equals(attribute.first)) {
                        
                        if (where) {
                            query.append(" WHERE ");
                            where = false;
                        } else {
                            query.append((close ? ")" : "" ) + " AND ");
                            close = false;
                        }

                        Object lowerBound = criteria.first, upperBound = criteria.second;
                        String lowerBoundName = attribute.first, upperBoundName = "";
                        if (attribute.second.lowerBound()) {

                            for (int j=i+1;j<modelData.attributesNumber;j++) {
                                if (modelData.attributes.elementAt(j).second.upperBound()) {
                                    upperBoundName = modelData.attributes.elementAt(j).first;
                                    break;
                                }
                            }

                            if (upperBoundName.equals("")) {
                                throw new IllegalStateException(
                                    "Couldn't find a name for the upper bound! Check your codebase for the class " + 
                                    modelData.getClass().getSimpleName() + "!"
                                );
                            }
                            
                            query
                                .append("(")
                                .append("(" + lowerBoundName + " BETWEEN ? AND ?) OR ")
                                .append("(" + upperBoundName + "BETWEEN ? AND ?) OR ")
                                .append("(" + lowerBoundName + " < ? AND " + upperBoundName + " > ?)")
                                .append(")");

                            inputs.add(lowerBound);
                            inputs.add(upperBound);
                            inputs.add(lowerBound);
                            inputs.add(upperBound);
                            inputs.add(lowerBound);
                            inputs.add(upperBound);

                        } else {
                            query.append(attribute.first + " BETWEEN ? AND ?");
                            inputs.add(lowerBound);
                            inputs.add(upperBound);
                        }

                        checkedBoundedCriterias++;
                    }
                }

                continue;
            }

            for (int j=0;j<discreteCriterias.size();j++) {

                Object curr = ( (Table) discreteCriterias.elementAt(j)).reflect.getAttribute(i);
                if (curr == null) {
                    continue;
                }

                if (where) {
                    query.append(" WHERE ");
                    where = false;
                } else if (currentAttribute == i) {
                    query.append(", ?");
                    inputs.add(curr);
                    continue;
                } else if (currentAttribute < i) {
                    query.append((close ? ")" : "" ) + " AND ");
                    close = false;
                }
                currentAttribute = i;

                String s = modelData.attributes.elementAt(i).first;
                if (modelData.attributes.elementAt(i).second.searchedText()) {
                    query.append(s + " LIKE ?");
                    inputs.add((String)curr+"%");
                } else {
                    query.append(s + " IN (?");
                    inputs.add(curr);
                    close = true;
                }
            }
        }
        query.append((close ? ")" : ""));

        return new Pair<>(query.toString() + ";", inputs);
    }

    private static Pair<String,Vector<Object>> scratch(Table tuple) {

        StringBuilder inputQuery = new StringBuilder("INSERT INTO " + tuple.sqliteTableName + "("); 
        StringBuilder valuesQuery = new StringBuilder("VALUES (");
        Vector<Object> inputs = new Vector<>();

        int n = tuple.attributesNumber;
        boolean first = true;
        for (int i=1;i<n;i++) {

            Object curr = tuple.reflect.getAttribute(i);
            if (curr == null) {
                continue;
            }

            inputQuery.append((first ? "" : ", ") + tuple.attributes.elementAt(i).first);
            valuesQuery.append((first ? "" : ", ") + "?");
            inputs.add(curr);
            first = false;
        }

        inputQuery.append(") ");
        valuesQuery.append(");");
        String pstmt = inputQuery.toString() + valuesQuery.toString();

        return new Pair<>(pstmt, inputs);
    }

    private Pair<String,Vector<Object>> scratch() {

        StringBuilder query = new StringBuilder("UPDATE " + sqliteTableName + " SET ");
        Vector<Object> inputs = new Vector<>();

        int n = attributesNumber;
        boolean first = true;
        for (int i=1;i<n;i++) {

            Object curr = reflect.getAttribute(i);
            if (curr == null) {
                continue;
            }

            query.append((!first ? ", " : "") + attributes.elementAt(i).first + " = ? "); 
            inputs.add(curr);
            first = false;
        }
        query.append("WHERE id=?;");
        inputs.add(this.id);

        return new Pair<>(query.toString(), inputs);
    }

    private static boolean db(String sqliteTableName) {

        boolean ans = false;

        File db = new File(path());
        if (db.exists() && db.isFile()) {
            String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + sqliteTableName + "';";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTable)) {

                ans = rs.next();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ans;
    }

    private static String path() {

        String url = "./ressources/databases/AutoRent.db";
        return url;
    }

    private static Table idToInstance(ResultSet rs, String className, String idColumnName) {

        try {
            return idToInstance(rs.getInt(idColumnName), className);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    } 

    protected static Table idToInstance(int id, String className) {

        Table i = ReflectionUtils.getModelInstance(className);
        i.id = id;

        Vector<Table> ic = search(i);
        if (ic.size() > 0) {
            return ic.elementAt(0);
        } else {
            return null;
        }
    }

    protected static LocalDate stringToDate(String s) {

        if (s.equals("")) {
            return null;
        }

        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + s);
        }
    }

    private static void setValues(PreparedStatement pstmt, Vector<Object> atts) {

        int i=1;
        try {
            for (Object att : atts) {
                if (att instanceof Table) {
                    pstmt.setInt(i, ((Table)att).getId());
                } else {
                    info.getSetter(att.getClass()).set(pstmt, i, att);
                }
                i++;
            }
        } catch (SQLException e) {
            System.err.println(pstmt + "\n" + atts.toString());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getValue(ResultSet rs, String columnName, Class<?> attributeClass) {

        if (Table.class.isAssignableFrom(attributeClass)) {
            return idToInstance(rs, attributeClass.getSimpleName(), columnName);
        } else {
            try {
                Object v = info.getGetter(attributeClass).get(rs, columnName);
                return rs.wasNull() ? null : v;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static Vector<Table> getTuples(PreparedStatement pstmt, String className) {

        Vector<Table> tuples = new Vector<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Table tuple = ReflectionUtils.getModelInstance(className);
                for (int i=0;i<tuple.attributesNumber;i++) {
                    tuple.reflect.setAttribute(i, getValue(rs, tuple.attributes.elementAt(i).first, tuple.reflect.getAttributeClass(i)));
                }
                tuples.add(tuple);
            }
        } catch (SQLException e) {
            System.err.println(pstmt.toString());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tuples;
    }

    static public class Info {

        public Map<String,String[]> modelAttributes;
        public String[] subClasses;
        static {
        }

        private Map<Class<?>,PreparedStatementSetter> javaClassPstmtSetter;
        private Map<Class<?>,ResultSetGetter> javaClassResultSetGetter;

        public Info() {

            javaClassResultSetGetter = new HashMap<>();
            javaClassPstmtSetter = new HashMap<>();
        }

        public void addModel(String name, String... attributes) {

            this.modelAttributes.put(name, attributes);
            subClasses = modelAttributes.keySet().toArray(String[]::new);
        }

        private void addType(Class<?> type, ResultSetGetter resultSetGetter, PreparedStatementSetter pstmtSetter) {

            javaClassPstmtSetter.put(type, pstmtSetter);
            javaClassResultSetGetter.put(type, resultSetGetter);
        }

        private PreparedStatementSetter getSetter(Class<?> type) {

            return javaClassPstmtSetter.get(type);
        }

        private ResultSetGetter getGetter(Class<?> type) {

            return javaClassResultSetGetter.get(type);
        }
    }

    @FunctionalInterface
    interface PreparedStatementSetter {

        public void set(PreparedStatement pstmt, int i, Object value) throws SQLException;
    }

    @FunctionalInterface
    interface ResultSetGetter {

        public Object get(ResultSet rs, String col) throws SQLException;
    }

}
