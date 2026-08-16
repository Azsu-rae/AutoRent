package model;

import static orm.annotation.Constraints.*;

import orm.Table;
import orm.annotation.Constraints;
import orm.annotation.Collection;

@Collection("academicLevels")
public class AcademicLevel extends Table {

    static {
        registerModel(AcademicLevel.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    Specialty specialty;

    @Constraints(type = INT, nullable = false)
    Integer level;

    public AcademicLevel() {
    }

    public AcademicLevel(Specialty specialty, Integer level) {
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
