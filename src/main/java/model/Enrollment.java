package model;

import static orm.annotation.Constraints.*;

import orm.Model;
import orm.Table;
import orm.annotation.Constraints;
import orm.annotation.Collection;

@Collection("enrollments")
public class Enrollment extends Table<Enrollment> {

    static {
        Model.register(Enrollment.class);
    }

    @Constraints(type = INT, foreignKey = true, nullable = false)
    private Student student;
    @Constraints(type = INT, foreignKey = true, nullable = false)
    private Course course;

    public static record Record() {
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
