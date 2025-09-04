package orm.model;

import orm.Table;

import orm.util.Pair;
import orm.util.Constraints;

import java.util.Vector;

import static orm.util.Reflection.getModelInstance;

public class User extends Table {

    static {
        registerModel(User.class);
    }

    @Constraints(type = "TEXT", nullable = false, searchedText = true)
    private String name;
    @Constraints(type = "TEXT", nullable = false, searchedText = true)
    private String surname;
    @Constraints(type = "TEXT", nullable = false)
    private String email;
    @Constraints(type = "TEXT", nullable = false)
    private String password;
    @Constraints(type = "TEXT", nullable = false)
    private String role;

    public User() {}

    public User(String name, String surname, String email, String password, String role) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static boolean isSearchable() {
        return isSearchable(new User());
    }

    public static Vector<Table> search() {
        return search(new User());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search(getModelInstance("User").reflect.setFieldValue(attName, value));
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {
        return search(new User(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new User());
        return search(tuples, boundedCriterias);
    }

    public User setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role;
    }
}
