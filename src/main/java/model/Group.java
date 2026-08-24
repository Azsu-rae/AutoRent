package model;

import orm.Table;
import orm.annotation.Constraints;
import orm.annotation.Collection;

import static orm.annotation.Constraints.*;

import orm.Model;

@Collection("groups")
public class Group extends Table<Group> {

    static {
        Model.register(Group.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private TeachingAssistant teachingAssistant;
    @Constraints(type = INT, nullable = false, foreignKey = true)
    private Section section;

    @Constraints(type = INT, nullable = false)
    private Integer number;

    public static record Record(int number) {
    }

    public Group() {
        super(Group.class);
    }

    public Group(TeachingAssistant teachingAssistant, Section section, Integer number) {
        this();
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
}
