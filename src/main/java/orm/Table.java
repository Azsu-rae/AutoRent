package orm;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import orm.annotation.Constraints;
import orm.annotation.Collection;
import util.BugDetectedException;
import util.Pair;

import static util.Log.*;
import static orm.annotation.Constraints.*;
import static orm.DataMapper.bindValues;
import static orm.DataMapper.fetchResutls;
import static orm.SQLiteQueryConstructor.DataDefinition.*;
import static orm.SQLiteQueryConstructor.DataManipulation.*;

public abstract class Table<T extends Table<T>> {

    public static void debug() {
    }

    public static final String dbPath;
    static {
        String enVar = System.getenv("AUTORENT_DB_PATH");
        if (enVar != null) {
            dbPath = Paths.get(enVar);
        } else {
            throw new BugDetectedException("Please set the AUTORENT_DB_PATH environment variable.");
        }
    }

    // ID given by the DB, so no setter
    @Constraints(type = INT, primaryKey = true)
    protected Integer id;

    public Integer getId() {
        return this.id;
    }

    // Reflection is used to access subclasse (model) specifics
    final SQLiteQueryConstructor query;
    final Model<T> model;

    public final Reflection reflect;

    protected Table(Class<T> klass) {
        this.model = Model.get(klass);
        this.reflect = new Reflection(this);
        this.query = new SQLiteQueryConstructor(this);
    }

    // print in a tree-like structure (to represent aggregations)
    @Override
    public String toString() {

        String s = new String(". " + this.getClass().getSimpleName() + "\n|\n+->");
        var attributes = new ArrayList<String>();

        for (var column : model.columns) {

            Object curr = reflect.field(column.name).getValue();
            if (curr == null) {
                continue;
            }

            if (Model.existsFor(curr.getClass())) {
                var lines = new List<String>();
                for (String line : curr.toString().split("\n")) {
                    lines.add(line);
                }
                s += String.join("\n|  ", lines);
                s += "|\n+->";
            } else {
                attributes.add(curr.toString());
            }
        }

        if (attributes.size() != 0) {
            s += " Attributes: (" + String.join(", ", attributes) + ")";
        } else {
            s += " EMPTY";
        }

        if (id != null) {
            s.append("\n+-> ID: " + id);
        }

        return s;
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

        Table<?> tuple = (Table<?>) obj;
        return this.id.equals(tuple.getId());
    }

    // CRUD operations: (Create, Read, Update, Delete) = (add, search, edit, delete)

    public static <T extends Table<T>> Vector<T> search(
            Class<T> klass,
            Vector<?> discreteCriterias,
            Vector<Range> boundedCriterias) {

        if (klass == null) {
            String s = "Class<T> cannot be null when searching!";
            throw new IllegalArgumentException(String.format(s));
        }

        var model = Model.get(klass);
        if (!db(model)) {
            String s = "No Database or no table found for the model '%s' while attempting a search!";
            throw new IllegalStateException(String.format(s, klass));
        }

        var preparedQuery = select(model, discreteCriterias, boundedCriterias);
        Vector<T> tuples = null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                PreparedStatement pstmt = conn.prepareStatement(preparedQuery.template())) {

            bindValues(pstmt, preparedQuery.values());
            tuples = fetchResutls(pstmt, model);
            sql("Ran query: %s", pstmt.toString());

        } catch (SQLException e) {
            throw new BugDetectedException(String.format("%s\n\nFor Query: %s", e, preparedQuery.template()));
        }

        return tuples;
    }

    public static void migrate(Model<?> model) {

        String tableDefinitionQuery = null;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                Statement stmt = conn.createStatement();) {

            tableDefinitionQuery = tableDefinitionQuery(model);
            stmt.execute(tableDefinitionQuery);

        } catch (SQLException e) {
            throw new BugDetectedException(
                    e + "\n\nTable creation query:\n\n" + tableName(model));
        }

        sql("Defined table %s with the query:\n\n%s", tableName(model), tableDefinitionQuery);
    }

    public int add() {

        if (!db()) {
            String s = "No database or no table found for the class: %s while attempting to add!";
            throw new IllegalStateException(String.format(s, getClass().getSimpleName()));
        }

        if (!isValid()) {
            fail("Invalid insertion attempt of the tuple:\n%s", this.toString());
            return 0;
        }

        for (var column : this.model.columns) {
            if (Model.existsFor(column.type)) {
                Table modelInstance = (Table) reflect.fields.get(fieldName);
                if (modelInstance != null && !Table.isTuple(modelInstance)) {
                    notice("Dependecy graph creation!");
                    if (modelInstance.add() == 0) {
                        return 0;
                    }
                }

            }
        }

        var preparedQuery = query.manipulate.insert();
        int affected = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                PreparedStatement pstmt = conn.prepareStatement(
                        preparedQuery.template(),
                        Statement.RETURN_GENERATED_KEYS)) {

            bindValues(pstmt, preparedQuery.values());
            affected = pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                this.id = keys.getInt(1);
            }

            sql("Ran query: %s\nGenerated key: %d", pstmt.toString(), this.id);

        } catch (SQLException e) {
            throw new BugDetectedException(String.format("%s\n\nInserttion query: %s", e, preparedQuery.template()));
        }

        return affected;
    }

    public int edit() {

        if (!db()) {
            String s = "No database or no table found for the class: %s while attempting editting!";
            throw new IllegalStateException(String.format(s, getClass().getSimpleName()));
        }

        if (!isValid() || id == null) {
            fail("Attempting to edit " + (!isValid() ? "whilst in invalid state" : "a non-tuple") + ": %s",
                    this.toString());
            return 0;
        }

        var statement = query.manipulate.update();
        int affected = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                PreparedStatement pstmt = conn.prepareStatement(statement.template())) {

            bindValues(pstmt, statement.values());
            affected = pstmt.executeUpdate();
            sql("Ran query: %s", pstmt.toString());

        } catch (SQLException e) {
            throw new BugDetectedException(String.format("%s\n\nUpdating query: %s", e, statement.template()));
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
            throw new BugDetectedException(String.format(s, getClass().getSimpleName(), this));
        }

        if (id == null) {
            return 0;
        }

        String sql = String.format("DELETE FROM %s WHERE id=?", query.define.tableName);
        int affected = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            affected = pstmt.executeUpdate();
            sql("Ran query: %s", pstmt.toString());

        } catch (SQLException e) {
            throw new BugDetectedException(String.format("%s\n\nDeletion query: %s", e, sql));
        }

        return affected;
    }

    static public boolean dbFile() {
        File db = new File(dbPath);
        return db.exists() && db.isFile();
    }

    public boolean db() {
        return db(model);
    }

    // checks if there's a DB and that the SQLite table is created
    public static boolean db(Model<?> model) {

        if (!dbFile()) {
            return false;
        }

        String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='%s';";
        boolean ans = false;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(String.format(checkTable, tableName(model)))) {

            ans = rs.next();

        } catch (SQLException e) {
            error(e);
            throw new BugDetectedException("Bad SQLite!");
        }

        return ans;
    }

    // checks if there are any non-nullable attributes that are, well, null
    public boolean isValid() {

        boolean valid = true;
        for (int i = 1; i < reflect.fields.count; i++) {
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
        }
        return true;
    }

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

    // Getting all the different values an enumerated attribute takes in the current
    // DB state
    public Set<String> getEnumeratedValuesOf(String att) {

        if (!reflect.fields.constraintsOf(att).enumerated()) {
            String s = "Attempting to get the values of an attribute that is not enumerated: %s";
            throw new IllegalArgumentException(String.format(s, att));
        }

        var tuples = search(getClass().getSimpleName());
        Set<String> values = new HashSet<>();
        for (var tuple : tuples) {
            values.add((String) tuple.reflect.fields.get(att));
        }

        return values;
    }

    // Model-related methods

    public static Set<Class<? extends Table>> getModels() {
        return Collections.unmodifiableSet(models);
    }

    public static List<String> getModelNames() {
        return getModels().stream().map(Class::getSimpleName).toList();
    }

    public boolean isFieldOfModel(String fieldName) {
        return hasSubClass(reflect.fields.typeOf(fieldName).getSimpleName());
    }

    public static String getModelNameFromCollectionName(String collectionName) {
        return Model.byCollectionName().get(collectionName).name;
    }

    public static boolean isKeyACollection(String key) {
        return Model.byCollectionName().keySet().contains(key);
    }

    // Used for to search for specific ranges
    static public class Range extends Pair<Object, Object> {

        // In the case of an attribute having a 'lowerBound' & 'upperBound', use the
        // lowerBound name
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

        // public boolean isValidCriteriaFor(Reflection r) {
        // return isValidCriteriaFor(r.fields);
        // }
        //
        // public boolean isValidCriteriaFor(Fields fields) {
        // return attributeName != null && first != null && second != null
        // && fields.visibleTypeOf(attributeName).equals(first.getClass())
        // && first.getClass().equals(second.getClass())
        // && fields.bounded.contains(attributeName);
        // }
    }
}
