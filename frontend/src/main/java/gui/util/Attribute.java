package gui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import orm.util.BugDetectedException;

public class Attribute<T> {

    final public List<T> values = new ArrayList<>();
    final public String ORMModelName;
    final public String name;

    public Attribute (String name) {
        this(null, name);
    }

    @SafeVarargs
    public Attribute(String ORMModelName, String name, T... values) {
        Arrays.asList(values).stream().forEach(this.values::add);
        this.ORMModelName = ORMModelName;
        this.name = name;
    }

    public Attribute<T> addValue(T value) {
        values.add(value);
        return this;
    }

    public T getSingleValue() {
        if (values.size() != 1) {
            throw new BugDetectedException("There shouldn't be any more or less than 1 here!");
        } return values.get(0);
    }
}
