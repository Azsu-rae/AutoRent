package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;
import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Model;
import orm.reflect.Reflected;

import static orm.annotate.Constraints.*;

@Collection("students")
public class Student extends Table<Student> {

    static {
        Model.register(Student.class, Student.Record.class);
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

    public static record Record(String surname, String name, String matricule, String email) implements Reflected<Student, RecordComponent> {
        @Override
        public Meta<Student, RecordComponent> meta() {
            return Model.of(Student.class).record;
        }
    }

    public Student() {
        super(Student.class);
    }

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
}
