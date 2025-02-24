package orm.model;

import utilities.Pair;
import orm.Table;
import java.util.Vector;

public class Vehicule extends Table<Vehicule> {

    private Double pricePerDay;
    private String state;
    private String maintenanceDate;

    private String brand;
    private String model;
    private Integer year;
    private String vehiculeType;
    private String fuelType;

    public Vehicule() {}

    public Vehicule(Double pricePerDay, String state, String maintenanceDate, Integer year, String brand, String model, String vehiculeType, String fuelType) {

        this.pricePerDay = pricePerDay;
        this.state = state;
        this.maintenanceDate = maintenanceDate;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vehiculeType = vehiculeType;
        this.fuelType = fuelType;
    }

    @Override
    protected int attributesNumber() {

        return 9;
    }

    @Override
    protected String sqliteTableName() {

        return "vehicules";
    }

    @Override
    protected String map(int i) {

        switch (i) {
            case 0: 
                return "id_vehicule";
            case 1: 
                return "prix_location_jour";
            case 2: 
                return "etat";
            case 3: 
                return "date_mantenance";
            case 4: 
                return "annee";
            case 5: 
                return "marque";
            case 6: 
                return "modele";
            case 7: 
                return "type";
            case 8: 
                return "carburant";
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
            case 1:
                return "d";
            case 4:
                return "i";
            default:
                return "s";
        }
    }

    @Override
    protected void setAttribute(Table tuple, int i, Object attributeValue) {

        Vehicule vehicule = (Vehicule) tuple;
        switch (i) {
            case 0: 
                vehicule.id = (Integer) attributeValue;
                break;
            case 1: 
                vehicule.pricePerDay = (Double) attributeValue;
                break;
            case 2: 
                vehicule.state = (String) attributeValue;
                break;
            case 3: 
                vehicule.maintenanceDate = (String) attributeValue;
                break;
            case 4: 
                vehicule.year = (Integer) attributeValue;
                break;
            case 5: 
                vehicule.brand = (String) attributeValue;
                break;
            case 6: 
                vehicule.model = (String) attributeValue;
                break;
            case 7: 
                vehicule.vehiculeType= (String) attributeValue;
                break;
            case 8: 
                vehicule.fuelType = (String) attributeValue;
                break;
            default:
        }
    }

    @Override
    protected boolean boundedAttribute(int i) {

        return i == 1 || i == 3 || i == 4;
    }

    @Override
    protected String table() {

        String table = "CREATE TABLE IF NOT EXISTS vehicules (" +
             "id_vehicule INTEGER PRIMARY KEY AUTOINCREMENT, " +
             "marque TEXT, " +
             "modele VARCHAR, " +
             "annee INTEGER, " +
             "type TEXT, " +
             "carburant TEXT, " +
             "prix_location_jour DECIMAL NOT NULL, " +
             "etat TEXT NOT NULL, " +
             "date_mantenance DATE NOT NULL" +
             ");";

        return table;
    }

    @Override
    protected Object attribute(int i) {

        switch (i) {
            case 0:
                return this.id;
            case 1:
                return this.pricePerDay;
            case 2:
                return this.state;
            case 3:
                return this.maintenanceDate;
            case 4:
                return this.year;
            case 5:
                return this.brand;
            case 6:
                return this.model;
            case 7:
                return this.vehiculeType;
            case 8:
                return this.fuelType;
            default:
                return null;
        }
    }

    @Override
    protected boolean isValid() {

        return pricePerDay != null && state != null && maintenanceDate != null;
    }

    public static Vector<Table> search(Object b1, Object b2) {

        return boundedSearch(new Vehicule(), b1, b2);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Vehicule());
        return search(tuples, boundedCriterias);
    }

    public Vehicule setPricePerDay(Double d) {

        this.pricePerDay = d;
        return this;
    }

    public Vehicule setState(String s) {

        this.state = s;
        return this;
    }

    public Vehicule setMaintenanceDate(String s) {

        this.maintenanceDate = s;
        return this;
    }

    public Vehicule setYear(Integer s) {

        this.year = s;
        return this;
    }

    public Vehicule setBrand(String s) {

        this.brand = s;
        return this;
    }

    public Vehicule setModel(String s) {

        this.model = s;
        return this;
    }

    public Vehicule setVehiculeType(String s) {

        this.vehiculeType = s;
        return this;
    }

    public Vehicule setFuelType(String s) {

        this.fuelType = s;
        return this;
    }

    public Double getPricePerDay() {

        return this.pricePerDay;
    }

    public String getState() {

        return this.state;
    }

    public String getMaintenanceDate() {

        return this.maintenanceDate;
    }

    public Integer getYear() {

        return this.year;
    }

    public String getBrand() {

        return this.brand;
    }

    public String getModel() {

        return this.model;
    }

    public String getVehiculeType() {

        return this.vehiculeType;
    }

    public String getFuelType() {

        return this.fuelType;
    }
}
