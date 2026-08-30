package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import static orm.annotate.Constraints.*;

import orm.Table;

import orm.reflect.Reflected;
import orm.reflect.Model;

import orm.annotate.Collection;
import orm.annotate.Constraints;

@Collection("enrollments")
public class Enrollment extends Table<Enrollment> {

    static {
        Model.register(Enrollment.class, Enrollment.Record.class);
    }

    @Constraints(type = INT, foreignKey = true, nullable = false)
    private Student student;
    @Constraints(type = INT, foreignKey = true, nullable = false)
    private Course course;

    public static record Record(String course_name, String student_matricule) implements Reflected<Enrollment, RecordComponent> {
        @Override
        public Meta<Enrollment, RecordComponent> meta() {
            return Model.of(Enrollment.class).record;
        }
    }

    public Enrollment() {
        super(Enrollment.class);
    }

    public Enrollment(Student student, Course course) {
        this();
        this.student = student;
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public Enrollment setStudent(Student student) {
        this.student = student;
        return this;
    }

    public Course getCourse() {
        return course;
    }

    public Enrollment setCourse(Course course) {
        this.course = course;
        return this;
    }
}
