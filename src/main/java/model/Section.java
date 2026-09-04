package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;

import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Model;
import orm.reflect.Reflected;

import static orm.annotate.Constraints.*;

@Collection("sections")
public class Section extends Table<Section> {

    static {
        Model.register(Section.class, Section.Record.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private AcademicLevel academicLevel;

    @Constraints(type = TEXT, nullable = false)
    private String identifier;

    public static record Record(String identifier) implements Reflected<Section, RecordComponent> {
        @Override
        public Meta<Section, RecordComponent> meta() {
            return Model.of(Section.class).record;
        }
    }

    public Section() {
        super(Section.class);
    }

    @Override public String toString() {
        return "S" + identifier;
    }

    public Section(AcademicLevel academicLevel, String identifier) {
        this();
        this.academicLevel = academicLevel;
        this.identifier = identifier;
    }

    public AcademicLevel getAcademicLevel() {
        return academicLevel;
    }

    public Section setAcademicLevel(AcademicLevel academicLevel) {
        this.academicLevel = academicLevel;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Section setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }
}
