package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static orm.util.Reflection.getModelInstance;
import static orm.util.Utils.format;

class DataMapper {

    private static Map<Class<?>,PreparedStatementSetter> javaClassPstmtSetter;
    private static Map<Class<?>,ResultSetGetter> javaClassResultSetGetter;

    static { // all of the types that any model can have
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

    static Class<?> setterType(Class<?> attribute) {
        if (attribute.equals(LocalDate.class)) {
            return String.class;
        }
        return attribute;
    }

    static void bindValues(PreparedStatement pstmt, Vector<Object> atts) throws SQLException {

        int i=1;
        for (Object att : atts) {
            if (att instanceof Table) {
                pstmt.setInt(i, ((Table)att).getId());
            } else {
                getSetter(att.getClass()).set(pstmt, i, att);
            }
            i++;
        }
    }

    static Vector<Table> fetchResutls(PreparedStatement pstmt, String className) throws SQLException {

        Vector<Table> tuples = new Vector<>();
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Table tuple = getModelInstance(className);
            for (int i=0;i<tuple.reflect.fields.count;i++) {
                String colName = tuple.query.getColumn(i).name;
                Class<?> attClass = tuple.reflect.fields.type(i);
                Object value = getValue(rs, colName, attClass);
                tuple.reflect.fields.set(i, value);
            }
            tuples.add(tuple);
        }

        return tuples;
    }

    private static Object getValue(ResultSet rs, String columnName, Class<?> attributeClass) throws SQLException {

        if (Table.class.isAssignableFrom(attributeClass)) {
            return idToInstance(rs.getInt(columnName), attributeClass.getSimpleName());
        } else {
            Object v = getGetter(attributeClass).get(rs, columnName);
            return rs.wasNull() ? null : v;
        }
    }

    private static Table idToInstance(int id, String className) {

        Table c = getModelInstance(className);
        c.id = id;

        Integer found = null;
        if (Table.isSearchable(c)) {
            Vector<Table> r = Table.search(c);
            found = r.size();
            if (r.size() > 0) {
                return r.elementAt(0);
            }
        }

        String s = "idToInstance exception: (isSearchable, size, className) = (%s, %s, %s)";
        throw new IllegalArgumentException(format(s, Table.isSearchable(c), found, className));
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
