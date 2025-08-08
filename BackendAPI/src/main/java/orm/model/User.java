package orm.model;

import orm.Table;

import utilities.Pair;
import utilities.Column;

import java.util.Vector;

public class User extends Table {

    @Column(type = "TEXT", nullable = false, searchedText = true)
    private String name;
    @Column(type = "TEXT", nullable = false, searchedText = true)
    private String surname;
    @Column(type = "TEXT", nullable = false)
    private String email;
    @Column(type = "TEXT", nullable = false)
    private String password;
    @Column(type = "TEXT", nullable = false)
    private String role;

    public User() {}

    public User(String name, String surname, String email, String password, String role) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static Vector<Table> search() {

        return search(new User());
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
