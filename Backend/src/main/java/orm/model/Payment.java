package orm.model;

import orm.Table;

import orm.util.Pair;
import orm.util.Constraints;

import java.time.LocalDate;
import java.util.Vector;

import static orm.util.Reflection.getModelInstance;

public class Payment extends Table {

    static {
        registerModel(Payment.class);
    }

    @Constraints(type = "INTEGER", nullable = false, foreignKey = true)
    private Reservation reservation;

    @Constraints(type = "DATE", nullable = false, bounded = true)
    private LocalDate date;
    @Constraints(type = "TEXT", nullable = false)
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

    public static boolean isSearchable() {
        return isSearchable(new Payment());
    }

    public static Vector<Table> search() {
        return search(new Payment());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search(getModelInstance("Payment").reflect.fields.set(attName, value));
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {
        return search(new Payment(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Payment());
        return search(tuples, boundedCriterias);
    }

    public Payment setReservation(Reservation r) {

        if (!isValidField(r)) {
            return this;
        }

        this.reservation = r;
        return this;
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
}
