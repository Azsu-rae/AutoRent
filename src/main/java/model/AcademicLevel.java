package model;

import static orm.annotation.Constraints.*;

import orm.Model;
import orm.Table;
import orm.annotation.Constraints;
import orm.annotation.Collection;

@Collection("academicLevels")
public class AcademicLevel extends Table<AcademicLevel> {

    static {
        Model.register(AcademicLevel.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    Specialty specialty;

    @Constraints(type = INT, nullable = false)
    Integer level;

    public static record Record(String specialty_acronyme, int level) {
    }

    public AcademicLevel() {
        super(AcademicLevel.class);
    }

    public AcademicLevel(Specialty specialty, Integer level) {
        this();
        this.setSpecialty(specialty);
        this.setLevel(level);
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public AcademicLevel setSpecialty(Specialty specialty) {
        this.specialty = specialty;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public AcademicLevel setLevel(Integer level) {
        this.level = level;
        return this;
    }
}
