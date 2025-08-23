package orm.model;

import orm.Table;

import orm.util.Pair;
import orm.util.Reflection;
import orm.util.Constraints;

import java.util.Vector;
import java.util.Objects;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation extends Table {

    static {
        registerModel(Reservation.class);
    }

    @Constraints(type = "INTEGER", nullable = false, foreignKey = true)
    private Client client;   
    @Constraints(type = "INTEGER", nullable = false, foreignKey = true)
    private Vehicle vehicle;

    @Constraints(type = "DECIMAL", nullable = false, bounded = true)
    private Double totalAmount;
    @Constraints(type = "DATE", nullable = false, lowerBound = true)
    private LocalDate startDate;
    @Constraints(type = "DATE", nullable = false, upperBound = true)
    private LocalDate endDate;
    @Constraints(type = "TEXT", nullable = false)
    private String status;

    public Reservation() {}

    public Reservation(Client client, Vehicle vehicle, String startDate, String endDate) {

        setClient(client);
        setVehicle(vehicle);
        this.startDate = stringToDate(startDate);
        this.endDate = stringToDate(endDate);

        setTotalAmountAndStatus();
    }

    public static boolean isSearchable() {

        return isSearchable(new Reservation());
    }

    public static Vector<Table> search() {

        return search(new Reservation());
    }

    public static Vector<Table> search(String attName, Object value) {

        return search(Reflection.getModelInstance("Reservation").reflect.setAttribute(attName, value));
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {

        return search(new Reservation(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Reservation());
        return search(tuples, boundedCriterias);
    }

    private void setTotalAmountAndStatus() {

        if (vehicle != null && startDate != null && endDate != null) {
            totalAmount = (ChronoUnit.DAYS.between(startDate, endDate)+1)*vehicle.getPricePerDay();
        }

        updateStatus();
    }

    public void updateStatus() {

        if (Objects.equals(status, "Canceled")) {
            return;
        }

        if (startDate != null && endDate != null && client != null && vehicle != null) {
            status = isOngoing() ? "Ongoing" : "In Effect";
        }
    }

    private boolean isOngoing() {

        LocalDate currentDate = LocalDate.now();
        return 
            (currentDate.isAfter(startDate) || currentDate.isEqual(startDate)) && 
            (currentDate.isBefore(endDate) || currentDate.isEqual(endDate));
    }

    public void cancel() {

        this.status = "Canceled";
    }

    public boolean rebook() {

        if (!isValid()) {
            throw new IllegalStateException("Rebooking a reservation of an invalid object!");
        }

        Reservation conflictingReservationsCriteria = new Reservation()
            .setStartDate(startDate.toString())
            .setEndDate(endDate.toString())
            .setVehicle(vehicle);

        if (search(conflictingReservationsCriteria).size() == 0) {
            return add(this);
        }

        return false;
    }

    public Reservation setClient(Client c) {

        if (c == null) {
            return this;
        }
        
        if (!c.isValid() || c.getId() == null) {
            throw new IllegalArgumentException("Invalid client:\n\n" + c + "\n");
        }

        this.client = c;
        setTotalAmountAndStatus();
        return this;
    }

    public Reservation setVehicle(Vehicle v) {

        if (v == null) {
            return this;
        }

        if (!v.isValid() || v.getId() == null) {
            throw new IllegalArgumentException("Invalid vehicle:\n\n" + v + "\n");
        }

        this.vehicle = v;
        setTotalAmountAndStatus();
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

    public Vehicle getVehicle() {

        return this.vehicle;
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
