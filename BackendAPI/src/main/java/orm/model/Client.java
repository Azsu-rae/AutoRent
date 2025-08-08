package orm.model;

import orm.Table;

import utilities.Pair;
import utilities.Column;

import java.util.Vector;

public class Client extends Table {

    @Column(type = "TEXT", nullable = false, searchedText = true)
    private String surname;
    @Column(type = "TEXT", nullable = false, searchedText = true)
    private String name;
    @Column(type = "TEXT", nullable = false)
    private String email;
    @Column(type = "TEXT", nullable = false)
    private String drivingLicence;
    @Column(type = "TEXT", nullable = false)
    private String phoneNumber;

    public Client() {}

    public Client(String name, String surname, String email, String phoneNumber, String drivingLicence) {

        this.name = name;
        this.surname = surname;
        this.drivingLicence = drivingLicence;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public static Vector<Table> search() {

        return search(new Client());
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {

        return search(new Client(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Client());
        return search(tuples, boundedCriterias);
    }

    public Client setSurname(String surname) {

        this.surname = surname;
        return this;
    }

    public Client setName(String name) {

        this.name = name;
        return this;
    }

    public Client setEmail(String email) {

        this.email = email;
        return this;
    }

    public Client setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
        return this;
    }

    public Client setDrivingLicence(String drivingLicence) {

        this.drivingLicence = drivingLicence;
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

    public String getPhoneNumber() {

        return this.phoneNumber;
    }

    public String getDrivingLicence() {

        return this.drivingLicence;
    }
}
