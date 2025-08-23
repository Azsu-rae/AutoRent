package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import orm.util.Reflection;

class DataMapper {

    private static Map<Class<?>,PreparedStatementSetter> javaClassPstmtSetter;
    private static Map<Class<?>,ResultSetGetter> javaClassResultSetGetter;

    // all of the types that any model can have
    static {
        javaClassPstmtSetter = new HashMap<>();
        javaClassResultSetGetter = new HashMap<>();
        addType(
            String.class, 
            ResultSet::getString,
            (pstmt, i, value) -> pstmt.setString(i, (String) value)
        );
        addType(
            Double.class, 
            ResultSet::getDouble,
            (pstmt, i, value) -> pstmt.setDouble(i, (Double) value)
        );
        addType(
            Integer.class, 
            ResultSet::getInt,
            (pstmt, i, value) -> pstmt.setInt(i, (Integer) value)
        );
        addType(
            LocalDate.class, 
            (rs, col) -> Table.stringToDate(rs.getString(col)),
            (pstmt, i, value) -> pstmt.setString(i, value.toString())
        );
    }

    static void bindValues(PreparedStatement pstmt, Vector<Object> atts) {

        int i=1;
        try {
            for (Object att : atts) {
                if (att instanceof Table) {
                    pstmt.setInt(i, ((Table)att).getId());
                } else {
                    getSetter(att.getClass()).set(pstmt, i, att);
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

    static Vector<Table> fetchResutls(PreparedStatement pstmt, String className) {

        Vector<Table> tuples = new Vector<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Table tuple = Reflection.getModelInstance(className);
                for (int i=0;i<tuple.reflect.getAttributesNumber();i++) {
                    String colName = tuple.query.getColumn(i).name;
                    Class<?> attClass = tuple.reflect.getAttributeClass(i);
                    Object value = getValue(rs, colName, attClass);
                    tuple.reflect.setAttribute(i, value);
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

    private static Table idToInstance(int id, String className) {

        Table i = Reflection.getModelInstance(className);
        i.id = id;

        Integer found = null;
        if (Table.isSearchable(i)) {
            Vector<Table> ic = Table.search(i);
            found = ic.size();
            if (ic.size() > 0) {
                return ic.elementAt(0);
            }
        }

        String s = String.format("(isSearchable, size, className) = (%s, %s, %s)", Table.isSearchable(i), found, className);
        throw new IllegalArgumentException("idToInstance exception: " + s);
    }

    private static Object getValue(ResultSet rs, String columnName, Class<?> attributeClass) {

        try {
            if (Table.class.isAssignableFrom(attributeClass)) {
                return idToInstance(rs.getInt(columnName), attributeClass.getSimpleName());
            } else {
                Object v = getGetter(attributeClass).get(rs, columnName);
                return rs.wasNull() ? null : v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void addType(Class<?> type, ResultSetGetter resultSetGetter, PreparedStatementSetter pstmtSetter) {

        javaClassPstmtSetter.put(type, pstmtSetter);
        javaClassResultSetGetter.put(type, resultSetGetter);
    }

    private static PreparedStatementSetter getSetter(Class<?> type) {

        return javaClassPstmtSetter.get(type);
    }

    private static ResultSetGetter getGetter(Class<?> type) {

        return javaClassResultSetGetter.get(type);
    }

    @FunctionalInterface
    private interface PreparedStatementSetter {

        public void set(PreparedStatement pstmt, int i, Object value) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetGetter {

        public Object get(ResultSet rs, String col) throws SQLException;
    }
}
