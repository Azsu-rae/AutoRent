package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import java.util.Vector;

@Collection("semesters")
public class Semester extends Table {

    static {
        registerModel(Semester.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private Specialty specialty;

    @Constraints(type = INT, nullable = false, foreignKey = true)
    Integer number;

    public Semester() {
    }

    public Integer getNumber() {
        return number;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public Semester setSpecialty(Specialty specialty) {
        this.specialty = specialty;
        return this;
    }

    public Semester setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public static boolean isSearchable() {
        return isSearchable("Semester");
    }

    public static Vector<Table> search() {
        return search(new Semester());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Semester", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new Semester(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Semester());
        return search(tuples, boundedCriterias);
    }
}
