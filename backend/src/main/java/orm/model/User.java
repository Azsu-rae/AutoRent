package orm.model;

import orm.Table;
import orm.util.Constraints;

import java.util.Vector;

public class User extends Table {

    static {
        registerModel(User.class);
    }

    @Constraints(type = "TEXT", nullable = false, searchedText = true)
    private String name;
    @Constraints(type = "TEXT", nullable = false, searchedText = true)
    private String surname;
    @Constraints(type = "TEXT", nullable = false, unique = true)
    private String email;
    @Constraints(type = "TEXT", nullable = false, unique = true)
    private String username;
    @Constraints(type = "TEXT", nullable = false)
    private String password;
    @Constraints(type = "TEXT", nullable = false, enumerated = true)
    private String role;

    public User() {}

    public User(String name, String surname, String email, String password, String role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static boolean authenticate(String id, String password) {
        return search(new User().setEmail(id).setPassword(password)).size() >= 1
            || search(new User().setUsername(id).setPassword(password)).size() >= 1;
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

    public User setUsername(String username) {
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return this.role;
    }

    public static boolean isSearchable() {
        return isSearchable("User");
    }

    public static Vector<Table> search() {
        return search(new User());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Payment", attName, value);
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {
        return search(new User(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new User());
        return search(tuples, boundedCriterias);
    }
}
