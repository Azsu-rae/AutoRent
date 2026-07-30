package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import java.util.Vector;

@Collection("semester_course_many_to_many")
public class SemesterCourse extends Table {

    static {
        registerModel(SemesterCourse.class);
    }

    public SemesterCourse() {
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    Semester semester;
    @Constraints(type = INT, nullable = false, foreignKey = true)
    Course course;

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

    public static boolean isSearchable() {
        return isSearchable("SemesterCourse");
    }

    public static Vector<Table> search() {
        return search(new SemesterCourse());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("SemesterCourse", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new SemesterCourse(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new SemesterCourse());
        return search(tuples, boundedCriterias);
    }
}
