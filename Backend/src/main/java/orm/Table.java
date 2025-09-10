package orm;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import static orm.util.Utils.*;
import static orm.util.Reflection.getModelInstance;

import static orm.DataMapper.bindValues;
import static orm.DataMapper.fetchResutls;

/**
 * <h2>Table Inheritance Rules</h2>
 *
 * <p>All subclasses of {@code Table} must follow these conventions:</p>
 *
 * <ol>
 *   <li>
 *     <b>Registration</b><br>
 *     Each subclass must register itself in a static block by calling
 *     {@code registerModel(Class<?>)}.
 *   </li>
 *
 *   <li>
 *     <b>Attributes</b><br>
 *     <ul>
 *       <li>Every attribute must correspond to a SQLite column.</li>
 *       <li>All SQLite columns must be annotated with {@code @Constraints}.</li>
 *     </ul>
 *   </li>
 *
 *   <li>
 *     <b>Constructors</b><br>
 *     Subclasses must provide constructors that accept string versions
 *     of all attributes (except when an attribute is itself a model type).
 *   </li>
 *
 *   <li>
 *     <b>Model-type attributes</b><br>
 *     Subclasses must check validity of all model-type attributes:
 *     <ul>
 *       <li>{@code null} values are permitted.</li>
 *       <li>Invalid tuples are not permitted.</li>
 *     </ul>
 *   </li>
 *
 *   <li>
 *     <b>Required Methods</b><br>
 *     Subclasses must implement the following methods:
 *     <pre>{@code
 *     public static boolean isSearchable();
 *     public static Vector<Table> search();
 *     public static Vector<Table> search(String attName, Object value);
 *     public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound);
 *     public static Vector<Table> searchRanges(Vector<Pair<Object, Object>> boundedCriterias);
 *     }</pre>
 *   </li>
 * </ol>
 */

public abstract class Table {

    private static String dbPath = "./Backend/ressources/databases/AutoRent.db";

    private static Set<Class<? extends Table>> models = new HashSet<>();
    protected static void registerModel(Class<? extends Table> model) {
        models.add(model);
    }

    @Constraints(type = "INTEGER", primaryKey = true)
    protected Integer id;
    public Integer getId() {
        return this.id;
    }

    final SQLiteQueryConstructor query;
    final public Reflection reflect;

    protected Table() {
        this.reflect = new Reflection(this);
        this.query = new SQLiteQueryConstructor(this);
    }

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

    public static boolean isSearchable(String modelName) {
        return isSearchable(getModelInstance(modelName));
    }

    public static boolean isSearchable(Table tuple) {
        return db(tuple.query.tableName);
    }

    public static Vector<Table> search(String className) {
        return search(getModelInstance(className));
    }

    public static Vector<Table> search(Table discreteCriteria) {
        return search(discreteCriteria, null, null, null);
    }

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias) {
        return search(discreteCriterias, null);
    }

    public static Vector<Table> search(
        Table discreteCriteria,
        String boundedAttributeName,
        Object lowerBound,
        Object upperBound) {

        if (discreteCriteria == null) {
            String s = "Give a discrete criteria when searching!";
            throw new IllegalArgumentException(format(s));
        }

        Vector<Table> discreteContainer = new Vector<>();
        discreteContainer.add(discreteCriteria);

        Vector<Pair<Object,Object>> boundedContainer = null;
        if (boundedAttributeName != null && lowerBound != null && upperBound != null) {
            boundedContainer = new Vector<>();
            boundedContainer.add(new Pair<>(boundedAttributeName, lowerBound, upperBound));
        }

        return search(discreteContainer, boundedContainer);
    }

    public static Vector<Table> search(
        Vector<? extends Table> discreteCriterias,
        Vector<Pair<Object,Object>> boundedCriterias) {

        if (discreteCriterias == null || discreteCriterias.size() == 0) {
            String s = "Give at least one discrete criteria when searching!";
            throw new IllegalArgumentException(format(s));
        }

        Table instance = discreteCriterias.elementAt(0);
        if (!db(instance.query.tableName)) {
            String s = "No Database or no table found for the model: %s while attempting a search!";
            throw new IllegalStateException(format(s, instance.getClass().getSimpleName()));
        }

        var scratched = instance.query.manipulate.select(discreteCriterias, boundedCriterias);
        Vector<Table> tuples = null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            bindValues(pstmt, scratched.second);
            tuples = fetchResutls(pstmt, instance.getClass().getSimpleName());

        } catch (SQLException e) {
            error(e, "Search query: %s", scratched.first);
        }

        return tuples;
    }

    public boolean add() {

        if (!isValid()) {
            String s = "Attempting to add an invalid tuple:\n\n%s";
            throw new IllegalArgumentException(format(s, this));
        }

        var scratched = query.manipulate.insert();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();) {

            stmt.execute(query.define.table());

            var pstmt = conn.prepareStatement(scratched.first);
            bindValues(pstmt, scratched.second);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {

            String[] suspects = {
                format("Table creation query:\n\n%s", query.define.table()),
                format("Insertion query: %s", scratched.first)
            };
            error(e, suspects);

            return false;
        }

        return true;
    }

    public boolean edit() {

        if (!db(query.tableName)) {
            String s = "No database or no table found for the class: %s while attempting deletion!";
            throw new IllegalStateException(format(s, getClass().getSimpleName()));
        }

        if (!isValid() || id == null) {
            String s = "Editting attempt on invalid object:\n\n%s";
            throw new IllegalArgumentException(format(s, this));
        }

        var scratched = query.manipulate.update();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            bindValues(pstmt, scratched.second);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            error(e, "Updating query: %s", scratched.first);
            return false;
        }

        return true;
    }

    public boolean delete() {

        if (!db(query.tableName)) {
            String s = "No database or no table found while attempting deletion for class: %s";
            throw new IllegalStateException(format(s, getClass().getSimpleName()));
        }

        if (id == null) {
            String s = "Pass an ID on a tuple you wish to delete:\n\n%s";
            throw new IllegalArgumentException(format(s, this));
        }

        if (!reflect.cascadeDeletion()) {
            String s = "Faillure to cascade deletion on this %s:\n\n%s";
            throw new IllegalStateException(format(s, getClass().getSimpleName(), this));
        }

        String sql = format("DELETE FROM %s WHERE id=?", query.tableName);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            error(e, "Deletion query: %s", sql);
            return false;
        }

        return true;
    }

    public static boolean hasSubClass(String className) {
        return getModelNames().contains(className);
    }

    public static List<String> getModelNames() {
        return getModels().stream().map(Class::getSimpleName).toList();
    }

    public static Set<Class<? extends Table>> getModels() {
        return Collections.unmodifiableSet(models);
    }

    private static boolean db(String sqliteTableName) {

        File db = new File(dbPath);
        if (!db.exists() || !db.isFile()) {
            return false;
        }

        String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='%s';";
        boolean ans = false;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(String.format(checkTable, sqliteTableName))) {

            ans = rs.next();

        } catch (SQLException e) {
            error(e);
        }

        return ans;
    }

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

    protected boolean isValidField(Table tuple) {

        if (tuple == null) {
            return false;
        }

        if (!tuple.isValid() || tuple.getId() == null) {
            String s = "Invalid %s:\n\n%s";
            throw new IllegalArgumentException(format(s, tuple.getClass().getSimpleName(), tuple));
        }

        return true;
    }

    protected static LocalDate stringToDate(String s) {

        if (s == null) {
            return null;
        }

        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + s);
        }
    }
}
