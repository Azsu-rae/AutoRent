package model;

import static orm.annotate.Constraints.*;

import java.lang.reflect.RecordComponent;

import orm.Table;

import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Meta;
import orm.reflect.Model;
import orm.reflect.Reflected;

@Collection("academicLevels")
public class AcademicLevel extends Table<AcademicLevel> {

    static {
        Model.register(AcademicLevel.class, AcademicLevel.Record.class);
    }

    @Constraints(type = INT, nullable = false)
    private Integer level;

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private Specialty specialty;

    public static record Record(int level, String specialty_acronyme) implements Reflected<AcademicLevel, RecordComponent> {
        @Override
        public Meta<AcademicLevel, RecordComponent> meta() {
            return Model.of(AcademicLevel.class).record;
        }
    }

    public AcademicLevel() {
        super(AcademicLevel.class);
    }

    @Override public String toString() {
        return "Year " + level;
    }

    public AcademicLevel(Integer level, Specialty specialty) {
        this();
        this.setSpecialty(specialty);
        this.setLevel(level);
    }

    public Integer getLevel() {
        return level;
    }

    public AcademicLevel setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public AcademicLevel setSpecialty(Specialty specialty) {
        this.specialty = specialty;
        return this;
    }
}
