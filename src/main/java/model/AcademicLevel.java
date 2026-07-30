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
    Integer number;

    public AcademicLevel() {
    }

    public AcademicLevel(Specialty specialty, Integer number) {
        this.setSpecialty(specialty);
        this.setNumber(number);
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public AcademicLevel setSpecialty(Specialty specialty) {
        this.specialty = specialty;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public AcademicLevel setNumber(Integer number) {
        this.number = number;
        return this;
    }

}
