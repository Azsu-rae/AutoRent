package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;

import orm.reflect.Reflected;
import orm.reflect.Model;

import orm.annotate.Collection;
import orm.annotate.Constraints;

import static orm.annotate.Constraints.*;

@Collection("courses")
public class Course extends Table<Course> {

    static {
        Model.register(Course.class, Course.Record.class);
    }

    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String name;

    public static record Record(String name) implements Reflected<Course, RecordComponent> {
        @Override
        public Meta<Course, RecordComponent> meta() {
            return Model.of(Course.class).record;
        }
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
