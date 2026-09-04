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
    V field;

    public Action(Reflected<U,V> instance, Meta<U,V> meta, V field) {
        this.instance = instance;
        this.meta = meta;
        this.field = field;

        try {
            this.getter = meta.getter(field);
            this.setter = meta.setter(field);
        } catch (NoSuchMethodException e) {
            String s = "FAH";
            if (this.getter == null && this.setter == null) {
                s = " both a getter and a setter";
            } else if (this.getter == null) {
                s = "getter";
            } else if (this.setter == null) {
                s = "setter";
            }
            throw new BugDetectedException("Trouble finding a %s for field %s", s, field);
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
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
            String s = "instance.getClass()=%s, field=%s, method=%s, args=%s";
            throw new BugDetectedException(s, instance.getClass(), field, method, arrayToString(args));
        }
    }
}
