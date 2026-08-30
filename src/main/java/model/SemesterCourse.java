package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;

import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Model;
import orm.reflect.Reflected;

import static orm.annotate.Constraints.*;

@Collection("semesterCourseManyToMany")
public class SemesterCourse extends Table<SemesterCourse> {

    static {
        Model.register(SemesterCourse.class, SemesterCourse.Record.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    Semester semester;
    @Constraints(type = INT, nullable = false, foreignKey = true)
    Course course;

    public static record Record(String course_name) implements Reflected<SemesterCourse, RecordComponent> {
        @Override
        public Meta<SemesterCourse, RecordComponent> meta() {
            return Model.of(SemesterCourse.class).record;
        }
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
