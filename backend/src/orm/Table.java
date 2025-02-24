package orm;

import utilities.Pair;
import orm.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.io.File;
import java.util.Vector;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class Table<T extends Table<T>> {

    protected Integer id;

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder("");

        s.append(".");
        if (this instanceof Vehicule) {
            s.append("Vehicule");
        } else if (this instanceof Client) {
            s.append("Client");
        } else if (this instanceof User) {
            s.append("User");
        } else if (this instanceof Reservation) {
            s.append("Reservation");
        } else if (this instanceof Return) {
            s.append("Return");
        } else if (this instanceof Payment) {
            s.append("Payment");
        }
        s.append("\n|\n+->");

        int n = attributesNumber();
        boolean first = true;
        for (int i=1;i<n;i++) {

            Object curr = attribute(i);
            if (curr == null) {
                continue;
            }

            if (curr instanceof Vehicule || curr instanceof Client || curr instanceof Reservation) {
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

        return this.id.equals(((Table)obj).getId());
    }

    protected abstract String sqliteTableName();

    protected abstract String table();

    protected abstract String map(int i);

    protected abstract String type(int i);

    protected abstract void setAttribute(Table tuple, int i, Object attributeValue);

    protected abstract boolean boundedAttribute(int i);

    protected abstract Object attribute(int i);

    protected abstract int attributesNumber();

    protected abstract boolean isValid();

    public static Vector<Table> search(Table tuple) {

        return boundedSearch(tuple, null, null);
    }

    public static Vector<Table> search(Table tuple, Object b1, Object b2) {

        return boundedSearch(tuple, b1, b2);
    }

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias) {

        return search(discreteCriterias, null);
    }

    protected static Vector<Table> boundedSearch(Table tuple, Object b1, Object b2) {

        if (tuple == null) {
            return null;
        }

        Vector<Table> dc = new Vector<>();
        dc.add(tuple);

        Vector<Pair<Object,Object>> bc = null;
        if (b1 != null && b2 != null) {
            bc = new Vector<>();
            bc.add(new Pair<>(b1, b2));
        } 

        return search(dc, bc);
    }

    public static Vector<Table> search(Vector<? extends Table> discreteCriterias, Vector<Pair<Object,Object>> boundedCriterias) {

        if (discreteCriterias == null || discreteCriterias.size() == 0) {
            return null;
        }

        String sqliteTableName = discreteCriterias.elementAt(0).sqliteTableName();
        if (!db(sqliteTableName)) {
            return null;
        }

        Pair<String,Vector<Object>> scratched = scratch(discreteCriterias, boundedCriterias);
        if (scratched == null) {
            return null;
        }

        Vector<Table> tuples = null;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            setValues(pstmt, scratched.second);
            tuples = getTuples(pstmt, sqliteTableName);

        } catch (SQLException e) {
            System.out.println("SQL error in search: " + e.getMessage());
            System.out.println(scratched.first);
        } catch (Exception e) {
            System.out.println("Unexpected error in search: " + e.getMessage());
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

            stmt.execute(tuple.table());

            PreparedStatement pstmt = conn.prepareStatement(scratched.first);
            setValues(pstmt, scratched.second);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println("SQL error during DB insertion: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during DB insertion: " + e.getMessage());
        }

        return true;
    }

    public boolean edit() {

        if (!isValid() || !db(sqliteTableName()) || id == null) {
            return false;
        }

        Pair<String,Vector<Object>> scratched = scratch();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
             PreparedStatement pstmt = conn.prepareStatement(scratched.first)) {

            setValues(pstmt, scratched.second);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error during edition: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during edition: " + e.getMessage());
        }

        return true;
    }

    public boolean delete() {

        if (!db(sqliteTableName()) || id == null) {
            return false;
        }

        String sql = "DELETE FROM " + sqliteTableName() + " WHERE " + map(0) + "=?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL error during deletion: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during deletion: " + e.getMessage());
        }

        return true;
    }

    public Integer getId() {

        return this.id;
    }

    private T setId(Integer id) {

        this.id = id;
        return (T) this;
    }

    private static Pair<String,Vector<Object>> scratch(Vector<? extends Table> discreteCriterias, Vector<Pair<Object,Object>> boundedCriterias) {

        Table data = discreteCriterias.elementAt(0);

        StringBuilder query = new StringBuilder("SELECT * FROM " + data.sqliteTableName());
        Vector<Object> inputs = new Vector<>();

        boolean close = false, where = true;
        int crit = 0, b = 0, n = data.attributesNumber(); 
        for (int i=0;i<n;i++) {

            if (data.boundedAttribute(i)) {

                if (boundedCriterias == null || b == boundedCriterias.size()) {
                    continue;
                }

                if (where) {
                    query.append(" WHERE ");
                    where = false;
                } else {
                    query.append((close ? ")" : "" ) + " AND ");
                    close = false;
                }
                crit = i;

                Object lowerBound = boundedCriterias.elementAt(b).first, upperBound = boundedCriterias.elementAt(b).second;
                String s = getBoundedCriteriaMap(data, lowerBound);
                if (s == null) {
                    System.out.println("We have a problem");
                    return null;
                }

                if (s.equals("date_debut")|| s.equals("date_fin")) {
                    query.append(
                        "(date_debut BETWEEN ? AND ?) OR " +
                        "(date_fin BETWEEN ? AND ?) OR " +
                        "(date_debut < ? AND date_fin > ?)"
                    );
                    inputs.add(lowerBound);
                    inputs.add(upperBound);
                    inputs.add(lowerBound);
                    inputs.add(upperBound);
                    inputs.add(lowerBound);
                    inputs.add(upperBound);
                } else {
                    query.append(s + " BETWEEN ? AND ?");
                    inputs.add(lowerBound);
                    inputs.add(upperBound);
                }

                b++;
                continue;
            }

            for (int j=0;j<discreteCriterias.size();j++) {

                Object curr = discreteCriterias.elementAt(j).attribute(i);
                if (curr == null) {
                    continue;
                }

                if (where) {
                    query.append(" WHERE ");
                    where = false;
                } else if (crit == i) {
                    query.append(", ?");
                    inputs.add(curr);
                    continue;
                } else if (crit < i) {
                    query.append((close ? ")" : "" ) + " AND ");
                    close = false;
                }
                crit = i;

                String s = data.map(i);
                if (s.equals("nom") || s.equals("prenom")) {
                    query.append("(nom LIKE ? OR prenom LIKE ?)");
                    inputs.add((String)curr+"%");
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

        StringBuilder inputQuery = new StringBuilder("INSERT INTO " + tuple.sqliteTableName() + "("); 
        StringBuilder valuesQuery = new StringBuilder("VALUES (");
        Vector<Object> inputs = new Vector<>();

        int n = tuple.attributesNumber();
        boolean first = true;
        for (int i=1;i<n;i++) {

            Object curr = tuple.attribute(i); 
            if (curr == null) {
                continue;
            }

            inputQuery.append((first ? "" : ", ") + tuple.map(i));
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

        StringBuilder query = new StringBuilder("UPDATE " + sqliteTableName() + " SET ");
        Vector<Object> inputs = new Vector<>();

        int n = attributesNumber();
        boolean first = true;
        for (int i=1;i<n;i++) {

            Object curr = attribute(i);
            if (curr == null) {
                continue;
            }

            query.append((!first ? ", " : "") + map(i) + " = ? "); 
            inputs.add(curr);
            first = false;
        }
        query.append("WHERE " + map(0) + " = ?;");
        inputs.add(this.id);

        return new Pair<>(query.toString(), inputs);
    }

    private static String getBoundedCriteriaMap(Table data, Object object) {

        String type = null;
        if (object instanceof Integer) {
            type = "i";
        } else if (object instanceof Double) {
            type = "d";
        } else if (object instanceof String) {
            type = "s";
        } 

        int n = data.attributesNumber();
        for (int i=0;i<n;i++) {

            String realType = data.type(i);
            if (realType.equals("l")) {
                realType = "s";
            }

            if (realType.equals(type) && data.boundedAttribute(i)) {
                return data.map(i);
            }
        }

        return null;
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
                System.out.println("SQL ERROR during database checking: " + e);
            }
        }

        return ans;
    }

    private static String path() {

        String url = "./backend/ressources/databases/AutoRent.db";
        return url;
    }

    private static void setValues(PreparedStatement pstmt, Vector<Object> atts) {

        int i=1;
        try {
            for (Object att : atts) {
                if (att instanceof Client) {
                    pstmt.setInt(i, ((Client)att).getId());
                } else if (att instanceof Vehicule) {
                    pstmt.setInt(i, ((Vehicule)att).getId());
                } else if (att instanceof Reservation) {
                    pstmt.setInt(i, ((Reservation)att).getId());
                } else if (att instanceof Double) {
                    pstmt.setDouble(i, (Double)att);
                } else if (att instanceof LocalDate) {
                    pstmt.setString(i, ((LocalDate)att).toString());
                } else if (att instanceof Integer) {
                    pstmt.setInt(i, (Integer)att);
                } else if (att instanceof String) {
                    pstmt.setString(i, (String)att);
                } else {
                    System.out.println("Error in object type while inserting the value in the Prepared Statement!");
                }
                i++;
            }
        } catch (SQLException e) {
            System.out.println("SQL error in prepared statement value setting: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error in prepared statement value setting: " + e.getMessage());
        }
    }

    private static Table createInstance(String sqliteTableName) {

        switch (sqliteTableName) {
            case "vehicules":
                return new Vehicule();
            case "clients":
                return new Client();
            case "reservations":
                return new Reservation();
            case "utilisateurs":
                return new User();
            case "retours":
                return new Return();
            case "paiements":
                return new Payment();
            default:
                return null;
        }
    }

    private static Table idToInstance(ResultSet rs, String sqliteTableName, String idColumnName) {

        try {
            Table i = createInstance(sqliteTableName).setId(rs.getInt(idColumnName));
            Vector<Table> ic = search(i);
            if (ic.size() > 0) {
                return ic.elementAt(0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR in idToInstance operation: " + e);
            return null;
        }
    } 

    protected static LocalDate stringToDate(String s) {

        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + s);
            return null;
        }
    }

    private static Object getValue(ResultSet rs, String columnName, String type) {

        try {
            switch (type) {
                case "c":
                    return idToInstance(rs, "clients", columnName);
                case "v":
                    return idToInstance(rs, "vehicules", columnName);
                case "r":
                    return idToInstance(rs, "reservations", columnName);
                case "d":
                    return rs.getDouble(columnName);
                case "l":
                    return stringToDate(rs.getString(columnName));
                case "i":
                    return rs.getInt(columnName);
                case "s":
                    return rs.getString(columnName);
                default:
                    return null;
            }
        } catch (SQLException e) {
            System.out.println("Error in getting the values from a ResultSet! " + e);
            return null;
        }
    }

    private static Vector<Table> getTuples(PreparedStatement pstmt, String sqliteTableName) {

        Vector<Table> tuples = new Vector<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Table tuple = createInstance(sqliteTableName);
                for (int i=0;i<tuple.attributesNumber();i++) {
                    tuple.setAttribute(tuple, i, getValue(rs, tuple.map(i), tuple.type(i)));
                }
                tuples.add(tuple);
            }
        } catch (SQLException e) {
            System.out.println("SQL error while getting tuples: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while getting tuples: " + e.getMessage());
        }

        return tuples;
    }
}
