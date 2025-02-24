package orm.model;

import utilities.Pair;
import orm.Table;
import java.util.Vector;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation extends Table<Reservation> {

    private Client client;   
    private Vehicule vehicule;

    private Double totalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public Reservation() {}

    public Reservation(Client client, Vehicule vehicule, String startDate, String endDate) {

        setClient(client);
        setVehicule(vehicule);
        this.startDate = stringToDate(startDate);
        this.endDate = stringToDate(endDate);

        setTotalAmountAndStatus();
    }

    private void setTotalAmountAndStatus() {

        if (vehicule != null && startDate != null && endDate != null) {
            totalAmount = (ChronoUnit.DAYS.between(startDate, endDate)+1)*vehicule.getPricePerDay();
        }

        if (startDate != null && endDate != null && client != null && vehicule != null) {
            status = "In Effect";
        }
    }

    @Override
    protected int attributesNumber() {

        return 7;
    }

    @Override
    protected String sqliteTableName() {

        return "reservations";
    }

    @Override
    protected String map(int i) {

        switch (i) {
            case 0: 
                return "id_reservation";
            case 1: 
                return "id_vehicule";
            case 2: 
                return "id_client";
            case 3: 
                return "date_debut";
            case 4: 
                return "date_fin";
            case 5: 
                return "montant_total";
            case 6: 
                return "status";
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
                return "c";
            case 2:
                return "v";
            case 3:
                return "l";
            case 4:
                return "l";
            case 5:
                return "d";
            default:
                return "s";
        }
    }

    @Override
    protected void setAttribute(Table tuple, int i, Object attributeValue) {

        Reservation reservation = (Reservation) tuple;
        switch (i) {
            case 0: 
                reservation.id = (Integer) attributeValue;
                break;
            case 1:
                reservation.client = (Client) attributeValue;
                break;
            case 2:
                reservation.vehicule = (Vehicule) attributeValue;
                break;
            case 3:
                reservation.startDate = (LocalDate) attributeValue;
                break;
            case 4:
                reservation.endDate = (LocalDate) attributeValue;
                break;
            case 5:
                reservation.totalAmount = (Double) attributeValue;
                break;
            case 6:
                reservation.status = (String) attributeValue;
                break;
            default:
        }
    }

    @Override
    protected boolean boundedAttribute(int i) {

        return i == 3 || i == 4 || i == 5;
    }

    @Override
    protected String table() {

        String table = "CREATE TABLE IF NOT EXISTS reservations (" +
            "id_reservation INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_vehicule INTEGER NOT NULL, " +
            "id_client INTEGER NOT NULL, " +
            "date_debut DATE NOT NULL, " +
            "date_fin DATE NOT NULL, " +
            "montant_total DECIMAL NOT NULL, " +
            "status VARCHAR NOT NULL, " +
            "FOREIGN KEY (id_vehicule) REFERENCES vehicules(id_vehicule), " +
            "FOREIGN KEY (id_client) REFERENCES clients(id_client)" +
            ");";

        return table;
    }

    @Override
    protected Object attribute(int i) {

        switch (i) {
            case 0:
                return this.id;
            case 1:
                return this.client;
            case 2:
                return this.vehicule;
            case 3:
                return this.startDate;
            case 4:
                return this.endDate;
            case 5:
                return this.totalAmount;
            case 6:
                return this.status;
            default:
                return null;
        }
    }

    @Override
    protected boolean isValid() {

        return client != null && vehicule != null && totalAmount != null && startDate != null && endDate != null && status != null;
    }

    public static Vector<Table> search(Object b1, Object b2) {

        return boundedSearch(new Reservation(), b1, b2);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Reservation());
        return search(tuples, boundedCriterias);
    }

    public Reservation setClient(Client c) {

        if (!c.isValid() || c.getId() == null) {
            return this;
        }

        this.client = c;
        setTotalAmountAndStatus();
        return this;
    }

    public Reservation setVehicule(Vehicule v) {

        if (!v.isValid() || v.getId() == null) {
            return this;
        }

        this.vehicule = v;
        setTotalAmountAndStatus();
        return this;
    }

    public Reservation setStatus(String s) {

        this.status = s;
        return this;
    }

    public Reservation setStartDate(String startDate) {

        this.startDate = stringToDate(startDate);
        setTotalAmountAndStatus();
        return this;
    }

    public Reservation setEndDate(String endDate) {

        this.endDate = stringToDate(endDate);
        setTotalAmountAndStatus();
        return this;
    }

    public Client getClient() {

        return this.client;
    }

    public Vehicule getVehicule() {

        return this.vehicule;
    }

    public String getStatus() {

        return this.status;
    }

    public Double getTotalAmount() {

        return this.totalAmount;
    }

    public String getStartDate() {

        return this.startDate.toString();
    }

    public String getEndDate() {

        return this.endDate.toString();
    }
}
