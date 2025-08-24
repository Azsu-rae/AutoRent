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

import static orm.DataMapper.bindValues;
import static orm.DataMapper.fetchResutls;

import static orm.util.Reflection.getModelInstance;

public abstract class Table {

    private static String path = "./BackendAPI/ressources/databases/AutoRent.db";

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

        int n = reflect.getAttributesNumber();
        boolean first = true;
        for (int i=1;i<n;i++) {

            Object curr = reflect.getAttribute(i);
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
        return db(tuple.query.sqliteTableName);
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

    public static Vector<Table> search(Table discreteCriteria, String boundedAttributeName, Object lowerBound, Object upperBound) {

        if (discreteCriteria == null) {
            throw new IllegalArgumentException("Give a discrete criteria when searching!");
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

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias, Vector<Pair<Object,Object>> boundedCriterias) {

        if (discreteCriterias == null || discreteCriterias.size() == 0) {
            throw new IllegalArgumentException("Give at least one discrete criteria when searching!");
        }

        Table instance = discreteCriterias.elementAt(0);
        if (!db(instance.query.sqliteTableName)) {
            throw new IllegalStateException("No Database or no table found!");
        }

        var scratched = instance.query.manipulate.select(discreteCriterias, boundedCriterias);
        Vector<Table> tuples = null;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            bindValues(pstmt, scratched.second);
            tuples = fetchResutls(pstmt, instance.getClass().getSimpleName());

        } catch (SQLException e) {
            System.err.println("Search query: " + scratched.first);
            e.printStackTrace();
        }

        return tuples;
    }

    public boolean add() {

        if (!isValid()) {
            return false;
        }

        var scratched = query.manipulate.insert();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
             Statement stmt = conn.createStatement();) {

            stmt.execute(query.define.table());

            var pstmt = conn.prepareStatement(scratched.first);
            bindValues(pstmt, scratched.second);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("Table creation query:\n" + query.define.table());
            System.err.println("Insertion query: " + scratched.first);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean edit() {

        if (!isValid() || !db(query.define.table()) || id == null) {
            return false;
        }

        var scratched = query.manipulate.update();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            bindValues(pstmt, scratched.second);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Updating query: " + scratched.first);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean delete() {

        if (!db(query.define.table()) || id == null) {
            return false;
        }

        boolean success = reflect.cascadeDeletion();
        String sql = "DELETE FROM " + query.sqliteTableName + " WHERE id=?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Deletion query: " + sql);
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    public boolean isValid() {

        boolean valid = true;
        for (int i=1;i<reflect.getAttributesNumber();i++) {
            Constraints col = reflect.getFieldConstraints()[i];
            if (!col.nullable() && reflect.getAttribute(i) == null) {
                valid = false;
                break;
            }
        }

        return valid;
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

    private static boolean db(String sqliteTableName) {

        boolean ans = false;

        File db = new File(path);
        if (db.exists() && db.isFile()) {
            String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + sqliteTableName + "';";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTable)) {

                ans = rs.next();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ans;
    }
}
