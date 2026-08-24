package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import orm.Model;

@Collection("semesters")
public class Semester extends Table<Semester> {

    static {
        Model.register(Semester.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private AcademicLevel academicLevel;

    @Constraints(type = INT, nullable = false, foreignKey = true)
    Integer number;

    public static record Record(int number) {
    }

    public Semester() {
        super(Semester.class);
    }

    public AcademicLevel getAcademicLevel() {
        return academicLevel;
    }

    public Semester setAcademicLevel(AcademicLevel academicLevel) {
        this.academicLevel = academicLevel;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Semester setNumber(Integer number) {
        this.number = number;
        return this;
    }
}
