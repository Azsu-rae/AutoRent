package orm.reflect;

import util.*;
import static util.Log.*;

import java.lang.reflect.*;

import orm.Table;

/*
 * U: ReflectedModel
 * V: Field/Component
 */
public class Action<U extends Table<U>,V> {

    Reflected<U,V> instance;
    Meta<U,V> meta;

    Method setter;
    Method getter;

    public Action(Reflected<U,V> instance, Meta<U,V> meta, V field) {
        this.instance = instance;
        this.meta = meta;

        try {
            this.getter = meta.getter(field);
            this.setter = meta.setter(field);
        } catch (NoSuchMethodException e) {
            throw new BugDetectedException("Trouble finding a getter/setter for field " + field);
        }
    }

    public Object getValue() {
        return invoke(getter);
    }

    public Reflected<U,V> setValue(Object value) {
        if (setter == null) {
            throw new UnsupportedOperationException("No setter was set, probably because this is a record.");
        } invoke(setter, value);
        return instance;
    }

    Object invoke(Method method, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            error(e.toString());
            throw new BugDetectedException("Bad Reflection Argument!");
        }
    }
}
