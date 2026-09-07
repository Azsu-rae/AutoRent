package orm.reflect;

import orm.Table;

/*
 * Reflection-based actions on either a tuple or a record instances
 *
 * U: ReflectedModel
 * V: Field/Component
 */
public interface Reflected<U extends Table<U>,V> {

    // TODO: maybe use this.getClass()?
    public abstract Meta<U,V> meta();

    public default Action<U,V> reflect(V v) {
        return new Action<U,V>(this, this.meta(), v);
    }
}
