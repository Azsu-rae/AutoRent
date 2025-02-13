package orm.model;

import utilities.Pair;
import orm.Table;
import java.util.Vector;

public class Payment extends Table<Payment> {

    private Reservation reservation;
    private String date;
    private String method;
    private Double amount;

    public Payment() {}

    public Payment(Reservation reservation, Double amount, String date, String method) {

        this.reservation = reservation;
        this.amount = amount;
        this.date = date;
        this.method = method;
    }

    @Override
    protected int attributesNumber() {

        return 5;
    }

    @Override
    protected String sqliteTableName() {

        return "paiements";
    }

    @Override
    protected String map(int i) {

        switch (i) {
            case 0:
                return "id_paiement";
            case 1:
                return "id_reservation";
            case 2:
                return "montant";
            case 3:
                return "date_paiement";
            case 4:
                return "mode_paiement";
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
                return "r";
            case 2:
                return "d";
            default:
                return "s";
        }
    }

    @Override
    protected void setAttribute(Table tuple, int i, Object attributeValue) {

        Payment payment = (Payment) tuple;
        switch (i) {
            case 0: 
                payment.id = (Integer) attributeValue;
                break;
            case 1: 
                payment.reservation = (Reservation) attributeValue;
                break;
            case 2: 
                payment.amount = (Double) attributeValue;
                break;
            case 3: 
                payment.date = (String) attributeValue;
                break;
            case 4: 
                payment.method = (String) attributeValue;
                break;
            default:
        }
    }

    @Override
    protected boolean boundedAttribute(int i) {

        return i == 2 || i == 3;
    }

    @Override
    protected String table() {

        String table = "CREATE TABLE IF NOT EXISTS paiements(" +
            "id_paiement INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_reservation INTEGER NOT NULL, " +
            "montant DECIMAL NOT NULL, " +
            "date_paiement DATE NOT NULL, " +
            "mode_paiement VARCHAR NOT NULL, " +
            "FOREIGN KEY (id_reservation) REFERENCES reservations(id_reservation)" +
            ");";

        return table;
    }

    @Override
    protected Object attribute(int i) {

        switch (i) {
            case 0:
                return this.id;
            case 1:
                return this.reservation;
            case 2:
                return this.amount;
            case 3:
                return this.date;
            case 4:
                return this.method;
            default:
                return null;
        }
    }

    @Override
    protected boolean isValid() {

        return reservation != null && amount != null && date != null && method != null;
    }

    public static Vector<Table> search(Object b1, Object b2) {

        return boundedSearch(new Payment(), b1, b2);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Payment());
        return search(tuples, boundedCriterias);
    }

    public Payment setReservation(Reservation r) {
        
        this.reservation = r;
        return this;
    }

    public Payment setDate(String date) {

        this.date = date;
        return this;
    }

    public Payment setMethod(String method) {

        this.method = method;
        return this;
    }

    public Payment setAmount(Double amount) {

        this.amount = amount;
        return this;
    }

    public Reservation getReservation() {
        
        return this.reservation;
    }

    public String getDate() {

        return this.date;
    }

    public String getMethod() {

        return this.method;
    }

    public Double getAmount() {

        return this.amount;
    }
}
