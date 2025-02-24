package orm.model;

import utilities.Pair;
import orm.Table;
import java.util.Vector;

public class Client extends Table<Client> {

    private String surname;
    private String name;
    private String email;
    private String drivingLicence;
    private String phoneNumber;

    public Client() {}

    public Client(String name, String surname, String email, String phoneNumber, String drivingLicence) {

        this.name = name;
        this.surname = surname;
        this.drivingLicence = drivingLicence;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    protected String sqliteTableName() {

        return "clients";
    }

    @Override
    protected String map(int i) {

        switch (i) {
            case 0: 
                return "id_client";
            case 1: 
                return "nom";
            case 2: 
                return "prenom";
            case 3: 
                return "email";
            case 4: 
                return "numero_permis";
            case 5: 
                return "telephone";
            default:
                System.out.println("WRONG INDEX PASSED TO MAP!");
                return "ERROR!";
        }
    }

    @Override
    protected String type(int i) {

        switch (i) {
            case 0:
                return "i";
            default:
                return "s";
        }
    }

    @Override
    protected void setAttribute(Table tuple, int i, Object attributeValue) {

        Client client = (Client) tuple;
        switch (i) {
            case 0: 
                client.id = (Integer) attributeValue;
                break;
            case 1: 
                client.surname= (String) attributeValue;
                break;
            case 2: 
                client.name = (String) attributeValue;
                break;
            case 3: 
                client.email = (String) attributeValue;
                break;
            case 4: 
                client.drivingLicence = (String) attributeValue;
                break;
            case 5: 
                client.phoneNumber = (String) attributeValue;
                break;
            default:
        }
    }

    @Override
    protected boolean boundedAttribute(int i) {

        return false;
    }

    @Override
    protected String table() {

        String table = "CREATE TABLE IF NOT EXISTS clients(" +
             "id_client INTEGER PRIMARY KEY AUTOINCREMENT, " +
             "nom TEXT NOT NULL, " +
             "prenom VARCHAR NOT NULL, " +
             "telephone VARCHAR NOT NULL, " +
             "email TEXT NOT NULL, " +
             "numero_permis TEXT NOT NULL " +
             ");";

        return table;
    }

    @Override
    protected Object attribute(int i) {

        switch (i) {
            case 0:
                return this.id;
            case 1:
                return this.surname;
            case 2:
                return this.name;
            case 3:
                return this.email;
            case 4:
                return this.drivingLicence;
            case 5:
                return this.phoneNumber;
            default:
                return null;
        }
    }

    @Override
    protected boolean isValid() {

        return name != null && surname != null && phoneNumber != null && email != null && drivingLicence != null;
    }

    @Override
    protected int attributesNumber() {

        return 6;
    }

    public static Vector<Table> search(Object b1, Object b2) {

        return boundedSearch(new Client(), b1, b2);
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
