package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;

import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Model;
import orm.reflect.Reflected;

import static orm.annotate.Constraints.*;

@Collection("groups")
public class Group extends Table<Group> {

    static {
        Model.register(Group.class, Group.Record.class);
    }

    @Constraints(type = INT, nullable = false)
    private Integer number;

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private TeachingAssistant teachingAssistant;
    @Constraints(type = INT, nullable = false, foreignKey = true)
    private Section section;

    public static record Record(int number, String teachingAssistant_email, String section_identifier) implements Reflected<Group, RecordComponent> {
        @Override
        public Meta<Group, RecordComponent> meta() {
            return Model.of(Group.class).record;
        }
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
