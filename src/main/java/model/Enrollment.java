package model;

import static orm.annotation.Constraints.*;

import java.util.Vector;

import orm.Table;
import orm.annotation.Constraints;
import orm.annotation.Collection;

@Collection("enrollments")
public class Enrollment extends Table {

    static {
        registerModel(Enrollment.class);
    }

    @Constraints(type = INT, foreignKey = true, nullable = false)
    private Student student;
    @Constraints(type = INT, foreignKey = true, nullable = false)
    private Course course;

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

    public static boolean isSearchable() {
        return isSearchable("Enrollment");
    }

    public static Vector<Table> search() {
        return search(new Enrollment());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Enrollment", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new Enrollment(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Enrollment());
        return search(tuples, boundedCriterias);
    }
}
