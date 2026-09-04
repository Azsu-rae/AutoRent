package orm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.List;

import orm.Table;

/**
 * U: ReflectedModel
 * V: Field/Component
 */
public abstract class Meta<U extends Table<U>,V> {

    public String nameOf(V v) {
        return switch (v) {
            case Field field -> Model.Fields.sqlName(field.getName(), field.getType());
            case RecordComponent recordComponent -> Model.Fields.sqlName(recordComponent.getName(), recordComponent.getType());
            default -> throw new IllegalArgumentException(v.getClass() + " is not a field!");
        };
    }

    public abstract Model<U> model();

    public abstract List<V> components();

    abstract Method getter(V v) throws NoSuchMethodException;

    abstract Method setter(V v) throws NoSuchMethodException;
}
