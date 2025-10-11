package gui.util;

import orm.util.Pair;

public class Attribute extends Pair<String,String[]> {

    public Attribute() {
        super(null, null);
    }

    private String model;
    public Attribute(String model, String name) {
        super(name, null);
        this.model = model;
    }

    public Attribute(String[] values) {
        this(null, values);
    }

    public Attribute(String value) {
        this(null, new String[] {value});
    }

    public Attribute(String name, String[] values) {
        super(name, values);
    }

    public String model() {
        return model;
    }

    public String name() {
        return first;
    }

    public String[] values() {
        return second;
    }

    public String value() {
        return second[0];
    }

    public Attribute setName(String name) {
        this.first = name;
        return this;
    }

    public Attribute setValue(String value) {
        this.second = new String[] {value};
        return this;
    }

    public Attribute setModel(String model) {
        this.model = model;
        return this;
    }
}
