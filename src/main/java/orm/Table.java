package orm;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;

import orm.annotate.Constraints;
import orm.reflect.Meta;
import orm.reflect.Model;
import orm.reflect.Reflected;

import util.BugDetectedException;
import util.Pair;

import static util.Log.*;

import static orm.DataMapper.bindValues;
import static orm.DataMapper.fetchResutls;

import static orm.SQLiteQueryConstructor.DataDefinition.tableName;
import static orm.SQLiteQueryConstructor.DataDefinition.tableDefinitionQuery;

import static orm.SQLiteQueryConstructor.DataManipulation.select;

// import static orm.SQLiteQueryConstructor.Dat

import static orm.annotate.Constraints.*;

public abstract class Table<T extends Table<T>> implements Reflected<T,Field> {

    public static final String dbPath;
    static {
        try {
            dbPath = Paths.get(System.getenv("AUTORENT_DB_PATH")).toString();
        } catch (NullPointerException _) {
            throw new BugDetectedException("Please set the AUTORENT_DB_PATH environment variable.");
        }
    }

    // ID given by the DB, so no setter
    @Constraints(type = INT, primaryKey = true)
    protected Integer id;

    public Integer getId() {
        return this.id;
    }

    private final SQLiteQueryConstructor query;
    public final Model<T> model;

    @Override
    public Meta<T,Field> meta() {
        return model;
    }

    protected Table(Class<T> klass) {
        this.model = Model.of(klass);
        this.query = new SQLiteQueryConstructor(this);
    }

    @Override
    public String toString() { // print in a tree-like structure (to represent aggregations)

        var s = new StringBuilder(". " + this.getClass().getSimpleName() + "\n|\n+->");
        var attributes = new ArrayList<String>();

        for (var column : model.columns) {

            Object curr = reflect(column.field).getValue();
            if (curr == null) {
                continue;
            }

            if (Model.existsFor(curr.getClass())) {
                s.append(String.join("\n|  ", curr.toString().split("\n")));
                s.append("|\n+->");
            } else {
                attributes.add(curr.toString());
            }
        }

        if (attributes.size() != 0) {
            s.append(" Attributes: (" + String.join(", ", attributes) + ")");
        } else {
            s.append(" EMPTY");
        }

        if (id != null) {
            s.append("\n+-> ID: " + id);
        }

        return s.toString();
    }

    @Override
    public boolean equals(Object obj) { // checks equality using the ID

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

    public static <T extends Table<T>> T withId(Model<T> model, int id) {

        if (model == null) {
            String s = "Model<T> cannot be null when searching!";
            throw new IllegalArgumentException(String.format(s));
        }

        if (!db(model)) {
            String s = "No Database or no table found for the model '%s' while attempting a search!";
            throw new IllegalStateException(String.format(s, model));
        }

        String sql = "SELECT * FROM " + tableName(model) + " WHERE id=?";
        List<T> tuples = null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(0, id);
            tuples = fetchResutls(pstmt, model);
            sql("Ran query: %s", pstmt.toString());

        } catch (SQLException e) {
            throw new BugDetectedException("%s\n\nFor Query: %s".formatted(e, sql));
        }

        return tuples.size() > 0 ? tuples.get(0) : null;
    }

    // --------------------------------------------------------------------------------
    // SEARCH
    //
    // each record instance is a criteria that can hold one enumeration of each model attribute
    // the moment we hit a 'null' it means that we ran out of enuerations for that specific attribute
    // e.g.
    //      Car('Toyota',   'Malibu',    'Good State')
    //      Car('Mercedes', 'Expensive', 'Excellent State')
    //      Car(null,    null,       'Poor State')
    // 
    // The state of the car has 3 possible values while the brand and type only have 2
    public static <T extends Table<T>,V> List<T> search(
            Meta<T,V> meta,
            List<Reflected<T,V>> discreteCriterias,
            List<Range> boundedCriterias) {

        if (meta == null) {
            String s = "Meta cannot be null when searching!";
            throw new IllegalArgumentException(String.format(s));
        }

        Model<T> model = meta.model();
        if (!db(model)) {
            String s = "No Database or no table found for the model '%s' while attempting a search!";
            throw new IllegalStateException(String.format(s, model.name));
        }

        var preparedQuery = select(meta, discreteCriterias, boundedCriterias);
        List<T> tuples = null;

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
            throw new BugDetectedException("%s\n\nTable creation query:\n\n".formatted(e, tableName(model)));
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

        int affected = 0;
        for (var column : this.model.columns) {
            if (Model.existsFor(column.type)) {
                Table<?> modelInstance = (Table<?>) reflect(column.field).getValue();
                if (modelInstance != null && !Table.isTuple(modelInstance)) {
                    notice("Dependecy graph creation!");
                    if (modelInstance.add() == 0) {
                        return affected;
                    } ++affected;
                }

            }
        }

        var preparedQuery = query.manipulate.insert();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                PreparedStatement pstmt = conn.prepareStatement(preparedQuery.template(), Statement.RETURN_GENERATED_KEYS)) {

            bindValues(pstmt, preparedQuery.values());
            affected = pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                this.id = keys.getInt(1);
            } else {
                error("NO GENERATED KEYS!");
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
            fail("Attempting to edit %s: %s".formatted((!isValid() ? "whilst in invalid state" : "a non-tuple"), this.toString()));
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

        if (id == null) {
            return 0;
        }

        String sql = "DELETE FROM %s WHERE id=?".formatted(tableName(model));
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

    // OVERLOADS

    public static <T extends Table<T>> List<T> search(Model<T> model, Field field, String value) {
        return Table.search(model, List.of(model.createInstance().reflect(field).setValue(value)), null);
    }

    // HELPERS

    public java.lang.Record asRecord() {
        // for (var column : model.columns) {
        //     reflect().field(column.field).getValue();
        // }
        // return model.record.construct();
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    // VERIFICATIONS

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
            error(e.toString());
            throw new BugDetectedException("Bad SQLite!");
        }

        return ans;
    }

    // checks if there are any non-nullable attributes that are, well, null
    public boolean isValid() {

        boolean valid = true;
        for (var column : model.columns) {
            if (!column.constraints.nullable() && reflect(column.field).getValue() == null) {
                valid = false;
                break;
            }
        }

        return valid;
    }

    // checks if it is a tuple, meaning a line from a sqlite table
    static public boolean isTuple(Table<?> tuple) {
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

    public static boolean checkMigration(Model<?> model) {
        return db(model);
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
