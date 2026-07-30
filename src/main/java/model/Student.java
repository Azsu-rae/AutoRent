package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import java.util.Vector;

@Collection("students")
public class Student extends Table {

    static {
        registerModel(Student.class);
    }

    @Constraints(type = INT, nullable = false, foreignKey = true)
    private Group group;

    @Constraints(type = TEXT, nullable = false)
    private String surname;
    @Constraints(type = TEXT)
    private String name;

    @Constraints(type = TEXT)
    private String matricule;
    @Constraints(type = TEXT)
    private String email;

    public Group getGroup() {
        return group;
    }

    public Student setGroup(Group group) {
        this.group = group;
        return this;
    }

    public String getName() {
        return name;
    }

    public Student setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public Student setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getMatricule() {
        return matricule;
    }

    public Student setMatricule(String matricule) {
        this.matricule = matricule;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Student setEmail(String email) {
        this.email = email;
        return this;
    }

    public static boolean isSearchable() {
        return isSearchable("Student");
    }

    public static Vector<Table> search() {
        return search(new Student());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Student", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new Student(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Student());
        return search(tuples, boundedCriterias);
    }
}
