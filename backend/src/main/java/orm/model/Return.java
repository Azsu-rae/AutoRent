package orm.model;

import orm.Table;

import orm.util.Constraints;

import java.time.LocalDate;
import java.util.Vector;

public class Return extends Table {

    static {
        registerModel(Return.class);
    }

    @Constraints(type = "INTEGER", nullable = false, foreignKey = true)
    private Reservation reservation;

    @Constraints(type = "DATE", nullable = false, bounded = true)
    private LocalDate returnDate;
    @Constraints(type = "TEXT", nullable = false, enumerated = true)
    private String returnState;
    @Constraints(type = "DECIMAL", nullable = false, bounded = true)
    private Double additionalFees;

    public Return() {}

    public Return(Reservation reservation, String returnDate, String returnState, Double additionalFees) {
        setReservation(reservation);
        this.returnDate = stringToDate(returnDate);
        this.returnState = returnState;
        this.additionalFees = additionalFees;
    }

    public Return setReservation(Reservation r) {
        if (r != null && r.isTupleOrElseThrow()) {
            this.reservation = r;
        } return this;
    }

    public Return setReturnDate(String returnDate) {
        this.returnDate = stringToDate(returnDate);
        return this;
    }

    public Return setReturnState(String returnState) {
        this.returnState = returnState;
        return this;
    }

    public Return setAdditionalFees(Double additionalFees) {
        this.additionalFees = additionalFees;
        return this;
    }

    public Reservation getReservation() {
        return this.reservation;
    }

    public String getReturnDate() {
        return this.returnDate.toString();
    }

    public String getReturnState() {
        return this.returnState;
    }

    public Double getAdditionalFees() {
        return this.additionalFees;
    }

    public static boolean isSearchable() {
        return isSearchable("Return");
    }

    public static Vector<Table> search() {
        return search(new Return());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Return", attName, value);
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {
        return search(new Return(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Return());
        return search(tuples, boundedCriterias);
    }
}
