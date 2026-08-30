package model;

import java.lang.reflect.RecordComponent;
import orm.reflect.Meta;

import orm.Table;
import orm.annotate.Collection;
import orm.annotate.Constraints;

import orm.reflect.Model;
import orm.reflect.Reflected;

import static orm.annotate.Constraints.*;

@Collection("teachingAssistants")
public class TeachingAssistant extends Table<TeachingAssistant> {

    static {
        Model.register(TeachingAssistant.class, TeachingAssistant.Record.class);
    }

    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String surname;
    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String name;

    @Constraints(type = TEXT, nullable = false, unique = true)
    private String email;
    @Constraints(type = TEXT, unique = true)
    private String phoneNumber;

    public static record Record(String surname, String name, String email, String phoneNumber) implements Reflected<TeachingAssistant, RecordComponent>  {
        @Override
        public Meta<TeachingAssistant, RecordComponent> meta() {
            return Model.of(TeachingAssistant.class).record;
        }
    }

    public TeachingAssistant() {
        super(TeachingAssistant.class);
    }

    public TeachingAssistant(String name, String surname, String email, String phoneNumber) {
        this();
        this.name = name;
        this.surname = surname;

        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public TeachingAssistant setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getName() {
        return name;
    }

    public TeachingAssistant setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public TeachingAssistant setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public TeachingAssistant setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}
