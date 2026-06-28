package model;

import orm.Table;
import orm.Constraints;
import orm.Constraints.Type;

import java.util.Vector;

public class TeachingAssistant extends Table {

    static {
        registerModel(TeachingAssistant.class);
    }

    @Constraints(type = Type.TEXT, nullable = false, searchedText = true)
    private String surname;
    @Constraints(type = Type.TEXT, nullable = false, searchedText = true)
    private String name;

    @Constraints(type = Type.TEXT, nullable = false, unique = true)
    private String email;
    @Constraints(type = Type.TEXT, nullable = false, unique = true)
    private String phoneNumber;

    public TeachingAssistant() {
    }

    public TeachingAssistant(String name, String surname, String email, String phoneNumber) {
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

    public static boolean isSearchable() {
        return isSearchable("TeachingAssistant");
    }

    public static Vector<Table> search() {
        return search(new TeachingAssistant());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("TeachingAssistant", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new TeachingAssistant(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new TeachingAssistant());
        return search(tuples, boundedCriterias);
    }
}
