package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import java.util.Set;
import java.util.Vector;

@Collection("specialties")
public class Specialty extends Table {

    static {
        registerModel(Specialty.class);
    }

    static final public String LICENCE = "Licence";
    static final public String MASTER = "Master";

    static final private Set<String> cyclePossibilities = Set.of(LICENCE, MASTER);

    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String name;
    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String acronyme;
    @Constraints(type = TEXT, nullable = false, enumerated = true)
    private String cycle;

    public Specialty() {
    }

    public Specialty(String name, String acronyme, String cycle) {
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
        return cycle;
    }

    public Specialty setCycle(String cycle) {
        if (!cyclePossibilities.contains(cycle)) {
            throw new IllegalArgumentException("Invalid cycle passed!");
        }

        this.cycle = cycle;
        return this;
    }

    public static boolean isSearchable() {
        return isSearchable("Specialty");
    }

    public static Vector<Table> search() {
        return search(new Specialty());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Specialty", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new Specialty(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Specialty());
        return search(tuples, boundedCriterias);
    }
}
