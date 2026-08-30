package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import orm.reflect.Model;

class DataMapper {

    static {
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

    static void bindValues(PreparedStatement pstmt, List<Object> values) throws SQLException {
        for (int i = 1; i <= values.size(); i++) {
            var att = values.get(i - 1);
            PstmtSetter.applyFor(att.getClass(), pstmt, att, i);
        }
    }

    static <T extends Table<T>> List<T> fetchResutls(PreparedStatement pstmt, Model<T> model) throws SQLException {

        List<T> tuples = new ArrayList<>();
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            T tuple = model.createInstance();
            for (var column : model.columns) {
                Object value = ResultSetGetter.of(column.type).get(rs, column.name);
                tuple.reflect(column.field).setValue(rs.wasNull() ? null : value);
            } tuples.add(tuple);
        }
        return tuples;
    }

    private static <T> void addType(Class<?> type, ResultSetGetter resultSetGetter, PstmtSetter<T> pstmtSetter) {
        PstmtSetter.pool.put(type, pstmtSetter);
        ResultSetGetter.pool.put(type, resultSetGetter);
    }

    @FunctionalInterface
    private interface PstmtSetter<T> {

        public void set(PreparedStatement pstmt, int i, T value) throws SQLException;

        final static Map<Class<?>, PstmtSetter<?>> pool = new HashMap<>();

        @SuppressWarnings("unchecked")
        private static <T> void applyFor(Class<T> type, PreparedStatement pstmt, Object att, int i) throws SQLException {
            if (att instanceof Table) {
                pstmt.setInt(i, ((Table<?>) att).getId());
            } else {
                ((PstmtSetter<T>) pool.get(att.getClass())).set(pstmt, i, (T) att);
            }
        }
    }

    @FunctionalInterface
    private interface ResultSetGetter {
        public Object get(ResultSet rs, String col) throws SQLException;

        final static Map<Class<?>, ResultSetGetter> pool = new HashMap<>();

        static ResultSetGetter of(Class<?> klass) {
            if (Table.class.isAssignableFrom(klass)) {
                return (rs, columnName) -> Table.withId(Model.ofWildcard(klass), rs.getInt(columnName));
            }
            return pool.get(klass);
        }
    }
}
