package model;

import orm.Table;
import orm.annotation.Collection;
import orm.annotation.Constraints;

import static orm.annotation.Constraints.*;

import orm.Model;

@Collection("teachingAssistants")
public class TeachingAssistant extends Table<TeachingAssistant> {

    static {
        Model.register(TeachingAssistant.class);
    }

    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String surname;
    @Constraints(type = TEXT, nullable = false, searchedText = true)
    private String name;

    @Constraints(type = TEXT, nullable = false, unique = true)
    private String email;
    @Constraints(type = TEXT, unique = true)
    private String phoneNumber;

    public static record Record(String surname, String name, String email, String phoneNumber) {
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
