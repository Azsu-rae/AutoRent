package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;

import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Model;
import orm.reflect.Reflected;

import static orm.annotate.Constraints.*;

@Collection("semesters")
public class Semester extends Table<Semester> {

    static {
        Model.register(Semester.class, Semester.Record.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private AcademicLevel academicLevel;

    @Constraints(type = INT, nullable = false)
    Integer number;

    public static record Record(int number) implements Reflected<Semester, RecordComponent> {
        @Override
        public Meta<Semester, RecordComponent> meta() {
            return Model.of(Semester.class).record;
        }
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
