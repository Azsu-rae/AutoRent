package model;

import orm.Constraints;
import orm.Table;

public class Specialty extends Table {

    static {
        registerModel(Specialty.class);
    }

    static final public String LICENCE = "Licence";
    static final public String MASTER = "Master";

    @Constraints(type = "TEXT")
    private String name;
    @Constraints(type = "TEXT")
    private String cycle;

    public Specialty(String name, String cycle) {
        this.name = name;
        this.cycle = cycle;
    }

    public String getName() {
        return name;
    }

    public Specialty setName(String name) {
        this.name = name;
        return this;
    }

    public String getCycle() {
        return cycle;
    }

    public Specialty setCycle(String cycle) {
        this.cycle = cycle;
        return this;
    }
}
