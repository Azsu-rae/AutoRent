package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import orm.Model;

@Collection("semester_course_many_to_many")
public class SemesterCourse extends Table<SemesterCourse> {

    static {
        Model.register(SemesterCourse.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    Semester semester;
    @Constraints(type = INT, nullable = false, foreignKey = true)
    Course course;

    public static record Record() {
    }

    public SemesterCourse() {
        super(SemesterCourse.class);
    }

    public Semester getSemester() {
        return semester;
    }

    public SemesterCourse setSemester(Semester semester) {
        this.semester = semester;
        return this;
    }

    public Course getCourse() {
        return course;
    }

    public SemesterCourse setCourse(Course course) {
        this.course = course;
        return this;
    }
}
