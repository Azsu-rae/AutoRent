package orm.model;

import orm.Table;

import utilities.Pair;
import utilities.Column;

import java.time.LocalDate;
import java.util.Vector;

public class Return extends Table {

    @Column(type = "INTEGER", nullable = false, foreignKey = true)
    private Reservation reservation;

    @Column(type = "DATE", nullable = false, bounded = true)
    private LocalDate returnDate;
    @Column(type = "TEXT", nullable = false)
    private String returnState;
    @Column(type = "DECIMAL", nullable = false, bounded = true)
    private Double additionalFees;

    public Return() {}

    public Return(Integer reservationId, String returnDate, String returnState, Double additionalFees) {

        this(reservationId.toString(), returnDate, returnDate, additionalFees.toString());
    }

    public Return(String reservationId, String returnDate, String returnState, String additionalFees) {

        this(
            (Reservation) idToInstance(Integer.parseInt(reservationId), "Reservation"), 
            returnDate, 
            returnState, 
            Double.parseDouble(additionalFees)
        );
    }

    public Return(Reservation reservation, String returnDate, String returnState, Double additionalFees) {

        setReservation(reservation);
        this.returnDate = stringToDate(returnDate);
        this.returnState = returnState;
        this.additionalFees = additionalFees;
    }


    public static Vector<Table> search() {

        return search(new Return());
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {

        return search(new Return(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Return());
        return search(tuples, boundedCriterias);
    }

    public Return setReservation(Reservation r) {

        if (!r.isValid() || r.getId() == null) {
            throw new IllegalArgumentException("Invalid Reservation!");
        }

        this.reservation = r;
        return this;
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
}
