package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class DataMapper {

    private static Map<Class<?>, PreparedStatementSetter<?>> javaClassPstmtSetter;
    private static Map<Class<?>, ResultSetGetter> javaClassResultSetGetter;

    static {
        javaClassPstmtSetter = new HashMap<>();
        javaClassResultSetGetter = new HashMap<>();
        addType(
                String.class,
                ResultSet::getString,
                PreparedStatement::setString);
        addType(
                Double.class,
                ResultSet::getDouble,
                PreparedStatement::setDouble);
        addType(
                Integer.class,
                ResultSet::getInt,
                PreparedStatement::setInt);
    }

    static void bindValues(PreparedStatement pstmt, Vector<Object> atts) throws SQLException {
        for (int i = 1; i <= atts.size(); i++) {
            var att = atts.elementAt(i - 1);
            if (att instanceof Table) {
                pstmt.setInt(i, ((Table<?>) att).getId());
            } else {
                getSetter(att.getClass()).set(pstmt, i, att);
            }
        }
    }

    static <T extends Table<T>> Vector<T> fetchResutls(PreparedStatement pstmt, Model<T> model) throws SQLException {

        Vector<T> tuples = new Vector<>();
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            T tuple = model.createInstance();
            for (var column : model.columns) {
                Object value = getValueFromResultSet(rs, column.name, column.type);
                tuple.reflect.field(column.name).setValue(value);
            }
            tuples.add(tuple);
        }
        return tuples;
    }

    private static Object getValueFromResultSet(ResultSet rs, String columnName, Class<?> attributeClass)
            throws SQLException {
        if (Table.class.isAssignableFrom(attributeClass)) {
            return idToInstance(rs.getInt(columnName), attributeClass.getSimpleName());
        } else {
            Object v = getGetter(attributeClass).get(rs, columnName);
            return rs.wasNull() ? null : v;
        }
    }

    // TODO: questionable place to do such an action
    private static Table<?> idToInstance(int id, String className) {

        Table c = getModelInstance(className);
        c.id = id;

        Integer found = null;
        if (Table.isSearchable(className)) {
            Vector<Table> r = Table.search(c);
            found = r.size();
            if (r.size() > 0) {
                return r.elementAt(0);
            }
        }

        String s = "idToInstance exception: (isSearchable, size, className) = (%s, %s, %s)";
        throw new IllegalArgumentException(String.format(s, Table.isSearchable(className), found, className));
    }

    private static <T> void addType(Class<?> type, ResultSetGetter resultSetGetter,
            PreparedStatementSetter<T> pstmtSetter) {
        javaClassPstmtSetter.put(type, pstmtSetter);
        javaClassResultSetGetter.put(type, resultSetGetter);
    }

    @FunctionalInterface
    private interface PreparedStatementSetter<T> {
        public void set(PreparedStatement pstmt, int i, T value) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetGetter {
        public Object get(ResultSet rs, String col) throws SQLException;
    }
}
