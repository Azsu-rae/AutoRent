package model;

import orm.Table;
import orm.annotation.Constraints;
import orm.annotation.Collection;

import static orm.annotation.Constraints.*;

import orm.Model;

@Collection("courses")
public class Course extends Table<Course> {

    static {
        Model.register(Course.class);
    }

    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String name;

    public static record Record(String name) {
    }

    public Course() {
        super(Course.class);
    }

    public Course(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Course setName(String name) {
        this.name = name;
        return this;
    }
}
