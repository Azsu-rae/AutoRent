package orm.model;

import orm.Table;
import orm.util.Constraints;

import java.util.Vector;

public class Client extends Table {

    static {
        registerModel(Client.class);
    }

    @Constraints(type = "TEXT", nullable = false, searchedText = true)
    private String surname;
    @Constraints(type = "TEXT", nullable = false, searchedText = true)
    private String name;
    @Constraints(type = "TEXT", nullable = false, unique = true)
    private String email;
    @Constraints(type = "TEXT", nullable = false, unique = true)
    private String drivingLicence;
    @Constraints(type = "TEXT", nullable = false, unique = true)
    private String phoneNumber;
    @Constraints(type = "TEXT", nullable = false, enumerated = true)
    private String nationality;

    public Client setNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }

    public Client() {}

    public Client(String name, String surname, String email, String phoneNumber, String drivingLicence) {
        this.name = name;
        this.surname = surname;
        this.drivingLicence = drivingLicence;
        this.phoneNumber = phoneNumber;
        this.email = email;
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

    public static boolean isSearchable() {
        return isSearchable("Client");
    }

    public static Vector<Table> search() {
        return search(new Client());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Client", attName, value);
    }

    public static Vector<Table> search(String boundedAttributeName, Object lowerBound, Object upperBound) {
        return search(new Client(), boundedAttributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Client());
        return search(tuples, boundedCriterias);
    }
}
