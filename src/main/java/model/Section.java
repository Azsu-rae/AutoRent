package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import java.util.Vector;

@Collection("sections")
public class Section extends Table {

    static {
        registerModel(Section.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private AcademicLevel academicLevel;

    @Constraints(type = INT, nullable = false)
    private Integer number;

    public Section(AcademicLevel academicLevel, Integer number) {
        this.academicLevel = academicLevel;
        this.number = number;
    }

    public Section() {
    }

    public AcademicLevel getAcademicLevel() {
        return academicLevel;
    }

    public Section setAcademicLevel(AcademicLevel academicLevel) {
        this.academicLevel = academicLevel;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Section setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public static boolean isSearchable() {
        return isSearchable("Group");
    }

    public static Vector<Table> search() {
        return search(new Group());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Group", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new Group(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Group());
        return search(tuples, boundedCriterias);
    }
}
