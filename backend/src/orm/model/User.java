package orm.model;

import utilities.Pair;
import orm.Table;
import java.util.Vector;

public class User extends Table<User> {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String role;

    public User() {}

    public User(String name, String surname, String email, String password, String role) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    protected int attributesNumber() {

        return 6;
    }

    @Override
    protected String sqliteTableName() {

        return "utilisateurs";
    }

    @Override
    protected String map(int i) {

        switch (i) {
            case 0: 
                return "id_utilisateur";
            case 1: 
                return "nom";
            case 2: 
                return "prenom";
            case 3: 
                return "email";
            case 4: 
                return "mot_de_passe";
            case 5: 
                return "role";
            default:
                System.out.println("WRONG INDEX PASSED TO MAP!");
                return "ERROR!";
        }
    }

    @Override
    protected String type(int i) {

        return i == 0 ? "i" : "s";
    }

    @Override
    protected void setAttribute(Table tuple, int i, Object attributeValue) {

        User user = (User) tuple;
        switch (i) {
            case 0:
                user.id = (Integer) attributeValue;
                break;
            case 1:
                user.name = (String) attributeValue;
                break;
            case 2:
                user.surname = (String) attributeValue;
                break;
            case 3:
                user.email = (String) attributeValue;
                break;
            case 4:
                user.password = (String) attributeValue;
                break;
            case 5:
                user.role = (String) attributeValue;
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

        String table = "CREATE TABLE IF NOT EXISTS utilisateurs(" +
            "id_utilisateur INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nom VARCHAR NOT NULL, " +
            "prenom VARCHAR NOT NULL, " +
            "email VARCHAR NOT NULL, " +
            "mot_de_passe VARCHAR NOT NULL, " +
            "role VARCHAR NOT NULL" +
            ");";

        return table;
    }

    @Override
    protected Object attribute(int i) {

        switch (i) {
            case 0:
                return id;
            case 1:
                return name;
            case 2:
                return surname;
            case 3:
                return email;
            case 4:
                return password;
            case 5:
                return role;
            default:
                return null;
        }
    }

    @Override
    protected boolean isValid() {

        return name != null && surname != null && email != null && password != null && role != null;
    }

    public static Vector<Table> search(Object b1, Object b2) {

        return boundedSearch(new User(), b1, b2);
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
