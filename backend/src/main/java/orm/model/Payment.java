package orm.model;

import orm.Table;

import orm.util.Constraints;

import java.time.LocalDate;
import java.util.Vector;

public class Payment extends Table {

    static {
        registerModel(Payment.class);
    }

    @Constraints(type = "INTEGER", nullable = false, foreignKey = true)
    private Reservation reservation;

    @Constraints(type = "DATE", nullable = false, bounded = true)
    private LocalDate date;
    @Constraints(type = "TEXT", nullable = false, enumerated = true)
    private String method;
    @Constraints(type = "DECIMAL", nullable = false, bounded = true)
    private Double amount;

    public Payment() {}

    public Payment(Reservation reservation, Double amount, String date, String method) {
        setReservation(reservation);
        this.amount = amount;
        this.date = stringToDate(date);
        this.method = method;
    }

    public Payment setReservation(Reservation r) {
        if (r != null && r.isTupleOrElseThrow()) {
            this.reservation = r;
        } return this;
    }

    public Payment setDate(String date) {
        this.date = stringToDate(date);
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
        return this.date.toString();
    }

    public String getMethod() {
        return this.method;
    }

    public Double getAmount() {
        return this.amount;
    }

    public static boolean isSearchable() {
        return isSearchable("Payment");
    }

    public static Vector<Table> search() {
        return search(new Payment());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Payment", attName, value);
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {
        return search(new Payment(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Payment());
        return search(tuples, boundedCriterias);
    }
}
