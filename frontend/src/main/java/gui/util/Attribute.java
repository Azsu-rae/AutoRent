package gui.util;

import orm.util.Pair;

public class Attribute extends Pair<String,String> {

    public Attribute(String values) {
        this(null, values);
    }

    public Attribute(String name, String values) {
        super(name, values);
    }

    public String name() {
        return first;
    }

    public String value() {
        return second;
    }
}
