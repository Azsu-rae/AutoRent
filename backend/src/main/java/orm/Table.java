package orm;

import java.io.File;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import orm.util.Constraints;
import orm.util.Pair;
import orm.util.Reflection;

import orm.model.*;

import static orm.util.Console.*;
import static orm.util.Reflection.getModelInstance;

import static orm.DataMapper.bindValues;
import static orm.DataMapper.fetchResutls;

public abstract class Table {

    // database path relative to the project's root directory
    private static String dbPath = "./Backend/ressources/databases/AutoRent.db";

    // loading subclasses into the JVM
    private static Set<Class<? extends Table>> models = new HashSet<>();
    static {
        Reflection.loadModels(new String[] {"Client", "Vehicle", "Reservation", "Return", "Payment", "User"});
    }

    // ID given by the DB, so no setter
    @Constraints(type = "INTEGER", primaryKey = true)
    protected Integer id;
    public Integer getId() {
        return this.id;
    }

    // Reflection is used to access subclasse (model) specifics
    public final Reflection reflect;
    final SQLiteQueryConstructor query;
    protected Table() {
        this.reflect = new Reflection(this);
        this.query = new SQLiteQueryConstructor(this);
    }

    // print in a tree-like structure (to represent aggregations)
    @Override
    public String toString() {

        StringBuilder s = new StringBuilder(". " + this.getClass().getSimpleName() + "\n|\n+->");

        boolean first = true;
        for (int i=1;i<reflect.fields.count;i++) {

            Object curr = reflect.fields.get(i);
            if (curr == null) {
                continue;
            }

            if (hasSubClass(curr.getClass().getSimpleName())) {
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

    // checks equality using the ID
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

        Table tuple = (Table) obj;
        return this.id.equals(tuple.getId());
    }

    // CRUD operations: (Create, Read, Update, Delete) = (add, search, edit, delete)

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias, Vector<Range> boundedCriterias) {

        if (discreteCriterias == null || discreteCriterias.size() == 0 || discreteCriterias.elementAt(0) == null) {
            String s = "Give at least one discrete criteria when searching!";
            throw new IllegalArgumentException(String.format(s));
        }

        Table instance = discreteCriterias.elementAt(0);
        if (!instance.db()) {
            String s = "No Database or no table found for the model: %s while attempting a search!";
            throw new IllegalStateException(String.format(s, instance.getClass().getSimpleName()));
        }

        var preparedQuery = instance.query.manipulate.select(discreteCriterias, boundedCriterias);
        Vector<Table> tuples = null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(preparedQuery.template())) {

            bindValues(pstmt, preparedQuery.values());
            tuples = fetchResutls(pstmt, instance.getClass().getSimpleName());

        } catch (SQLException e) {
            error(e, "Search query: %s", preparedQuery.template());
        }

        return tuples;
    }

    public int add() {

        if (!isValid()) {
            return 0;
        }

        var preparedQuery = query.manipulate.insert();
        int affected = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();) {

            stmt.execute(query.define.table());

            var pstmt = conn.prepareStatement(preparedQuery.template());
            bindValues(pstmt, preparedQuery.values());
            affected = pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            error(e, new String[] {
                String.format("Table creation query:\n\n%s", query.define.table()),
                String.format("Insertion query: %s", preparedQuery.template())
            });
        }

        return affected;
    }

    public int edit() {

        if (!db()) {
            String s = "No database or no table found for the class: %s while attempting editting!";
            throw new IllegalStateException(String.format(s, getClass().getSimpleName()));
        }

        if (!isValid() || id == null) {
            return 0;
        }

        var statement = query.manipulate.update();
        int affected = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(statement.template())) {

            bindValues(pstmt, statement.values());
            affected = pstmt.executeUpdate();

        } catch (SQLException e) {
            error(e, "Updating query: %s", statement.template());
        }

        return affected;
    }

    public int delete() {

        if (!db()) {
            String s = "No database or no table found while attempting deletion for class: %s";
            throw new IllegalStateException(String.format(s, getClass().getSimpleName()));
        }

        if (!reflect.cascadeDeletion()) {
            String s = "Faillure to cascade deletion on this %s:\n\n%s";
            throw new IllegalStateException(String.format(s, getClass().getSimpleName(), this));
        }

        if (id == null) {
            return 0;
        }

        String sql = String.format("DELETE FROM %s WHERE id=?", query.tableName);
        int affected = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            affected = pstmt.executeUpdate();

        } catch (SQLException e) {
            error(e, "Deletion query: %s", sql);
        }

        return affected;
    }

    // Verification methods

    // checks if there's a DB and that the SQLite table is created
    public boolean db() {

        File db = new File(dbPath);
        if (!db.exists() || !db.isFile()) {
            return false;
        }

        String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='%s';";
        boolean ans = false;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(String.format(checkTable, query.tableName))) {

            ans = rs.next();

        } catch (SQLException e) {
            error(e);
        }

        return ans;
    }

    // checks if there are any non-nullable attributes that are, well, null
    public boolean isValid() {

        boolean valid = true;
        for (int i=1;i<reflect.fields.count;i++) {
            Constraints col = reflect.fields.constraints[i];
            if (!col.nullable() && reflect.fields.get(i) == null) {
                valid = false;
                break;
            }
        }

        return valid;
    }

    // checks if it is a tuple, meaning a line from a sqlite table
    static public boolean isTuple(Table tuple) {
        return tuple.isValid() && tuple.getId() != null;
    }

    // throws an exception if it's not
    public boolean isTupleOrElseThrow() {
        if (!isTuple(this)) {
            String s = "Illegal attempt of insertion! Invalid %s:\n\n%s";
            throw new IllegalArgumentException(String.format(s, getClass().getSimpleName(), this));
        } return true;
    }

    // wrapper arround the db()
    public static boolean isSearchable(String modelName) {
        return getModelInstance(modelName).db();
    }

    // Utilities

    // accepts null values but throws at invalid formats
    public static LocalDate stringToDate(String s) {

        if (s == null || s.equals("")) {
            return null;
        }

        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + s);
        }
    }

    // Getting all the different values a specific attribute can take
    public Set<String> getAttributeValues(String att) {

        if (!reflect.fields.constraintsOf(att).enumerated()) {
            String s = "Attempting to get the values of an attribute that is not enumerated: %s";
            throw new IllegalArgumentException(String.format(s, att));
        }

        Set<String> values = new HashSet<>();
        var tuples = search(getClass().getSimpleName());
        for (var tuple : tuples) {
            values.add((String)tuple.reflect.fields.get(att));
        } return values;
    }

    // Model-related methods

    protected static void registerModel(Class<? extends Table> model) {
        models.add(model);
    }

    public static Set<Class<? extends Table>> getModels() {
        return Collections.unmodifiableSet(models);
    }

    public static List<String> getModelNames() {
        return getModels().stream().map(Class::getSimpleName).toList();
    }

    public static boolean hasSubClass(String className) {
        return getModelNames().contains(className);
    }

    // Overloads for convenience

    public static Vector<Table> search(String className) {
        return search(getModelInstance(className));
    }

    public static Vector<Table> search(String modelName, String attName, Object value) {
        return search(getModelInstance(modelName).reflect.fields.setDiscrete(attName, value));
    }

    public static Vector<Table> search(Table discreteCriteria) {
        return search(discreteCriteria, null, null, null);
    }

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias) {
        return search(discreteCriterias, null);
    }

    public static Vector<Table> search(Table discrete, String boundedName, Object lowerBound, Object upperBound) {

        Vector<Table> discreteContainer = new Vector<>();
        discreteContainer.add(discrete);

        Vector<Range> boundedContainer = null;
        if (boundedName != null && lowerBound != null && upperBound != null) {
            boundedContainer = new Vector<>();
            boundedContainer.add(new Range(boundedName, lowerBound, upperBound));
        }

        return search(discreteContainer, boundedContainer);
    }

    // Used for to search for specific ranges
    static public class Range extends Pair<Object,Object> {

        // In the case of an attribute having a 'lowerBound' & 'upperBound', use the lowerBound name
        public String attributeName;

        public Range(String attributeName, Object lowerBound, Object upperBound) {
            super(lowerBound, upperBound);
            this.attributeName = attributeName;
        }

        @Override
        public String toString() {
            return attributeName + " = " + super.toString();
        }

        public Object lowerBound() {
            return first;
        }

        public Object upperBound() {
            return second;
        }

        public boolean isValidCriteriaFor(Reflection r) {
            return
                attributeName != null && first != null && second != null
                && r.fields.visibleTypeOf(attributeName).equals(first.getClass())
                && first.getClass().equals(second.getClass())
                && r.fields.bounded.contains(attributeName);
        }
    }
}
