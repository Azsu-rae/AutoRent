package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;
import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Model;
import orm.reflect.Reflected;

import static orm.annotate.Constraints.*;

@Collection("specialties")
public class Specialty extends Table<Specialty> {

    static {
        Model.register(Specialty.class, Specialty.Record.class);
    }

    enum Cycle {
        LICENCE,
        MASTER
    }

    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String name;
    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String acronyme;
    @Constraints(type = TEXT, nullable = false, enumerated = true)
    private Cycle cycle;

    public static record Record(String name, String acronyme, Cycle cycle) implements Reflected<Specialty, RecordComponent> {
        @Override
        public Meta<Specialty, RecordComponent> meta() {
            return Model.of(Specialty.class).record;
        }
    }

    public Specialty() {
        super(Specialty.class);
    }

    public Specialty(String name, String acronyme, String cycle) {
        this();
        this.setName(name);
        this.setAcronyme(acronyme);
        this.setCycle(cycle);
    }

    public String getAcronyme() {
        return acronyme;
    }

    public Specialty setAcronyme(String acronyme) {
        this.acronyme = acronyme;
        return this;
    }

    public String getName() {
        return name;
    }

    public Specialty setName(String name) {
        this.name = name;
        return this;
    }

    public String getCycle() {
        switch (cycle) {
            case LICENCE:
                return "Licence";
            case MASTER:
                return "Master";
            default:
                return null;
        }
    }

    public Specialty setCycle(String cycle) {
        switch (cycle) {
            case "Licence":
                this.cycle = Cycle.LICENCE;
                break;
            case "Master":
                this.cycle = Cycle.MASTER;
                break;
            default:
                throw new IllegalArgumentException(cycle + " is not a valid specialty cycle!");
        }

        return this;
    }
}
