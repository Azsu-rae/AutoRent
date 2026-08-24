package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import orm.Model;

@Collection("sections")
public class Section extends Table<Section> {

    static {
        Model.register(Section.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private AcademicLevel academicLevel;

    @Constraints(type = TEXT, nullable = false)
    private String identifier;

    public static record Record(String identifier) {
    }

    public Section() {
        super(Section.class);
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
