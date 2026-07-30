package model;

import orm.Table;
import orm.annotation.Constraints;
import orm.annotation.Collection;

import static orm.annotation.Constraints.*;

import java.util.Vector;

@Collection("groups")
public class Group extends Table {

    static {
        registerModel(Group.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private TeachingAssistant teachingAssistant;
    @Constraints(type = INT, nullable = false, foreignKey = true)
    private Section section;

    @Constraints(type = INT, nullable = false)
    private Integer number;

    public Group() {
    }

    public Group(TeachingAssistant teachingAssistant, Section section, Integer number) {
        this.teachingAssistant = teachingAssistant;
        this.section = section;
        this.number = number;
    }

    public Section getSection() {
        return section;
    }

    public Group setSection(Section section) {
        this.section = section;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Group setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public TeachingAssistant getTeachingAssistant() {
        return teachingAssistant;
    }

    public Group setTeachingAssistant(TeachingAssistant teachingAssistant) {
        this.teachingAssistant = teachingAssistant;
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
