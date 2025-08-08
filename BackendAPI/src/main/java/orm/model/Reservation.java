package orm.model;

import orm.Table;

import utilities.Pair;
import utilities.Column;

import java.util.Vector;
import java.util.Objects;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation extends Table {

    @Column(type = "INTEGER", nullable = false, foreignKey = true)
    private Client client;   
    @Column(type = "INTEGER", nullable = false, foreignKey = true)
    private Vehicle vehicle;

    @Column(type = "DECIMAL", nullable = false, bounded = true)
    private Double totalAmount;
    @Column(type = "DATE", nullable = false, lowerBound = true)
    private LocalDate startDate;
    @Column(type = "DATE", nullable = false, upperBound = true)
    private LocalDate endDate;
    @Column(type = "TEXT", nullable = false)
    private String status;

    public Reservation() {}

    public Reservation(Integer clientId, Integer vehicleId, String startDate, String endDate) {

        this(
            clientId.toString(), 
            vehicleId.toString(), 
            startDate, 
            endDate
        );
    }

    public Reservation(String clientId, String vehicleId, String startDate, String endDate) {

        this(
            (Client) idToInstance(Integer.parseInt(clientId), "Client"), 
            (Vehicle) idToInstance(Integer.parseInt(vehicleId), "Vehicle"), 
            startDate, 
            endDate
        );
    }

    public Reservation(Client client, Vehicle vehicle, String startDate, String endDate) {

        setClient(client);
        setVehicle(vehicle);
        this.startDate = stringToDate(startDate);
        this.endDate = stringToDate(endDate);

        setTotalAmountAndStatus();
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

    public static Vector<Table> search() {

        return search(new Reservation());
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {

        return search(new Reservation(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Reservation());
        return search(tuples, boundedCriterias);
    }

    public Reservation setClient(Client c) {

        if (!c.isValid() || c.getId() == null) {
            throw new IllegalArgumentException("Invalid client!");
        }

        this.client = c;
        setTotalAmountAndStatus();
        return this;
    }

    public Reservation setVehicle(Vehicle v) {

        if (!v.isValid() || v.getId() == null) {
            throw new IllegalArgumentException("Invalid vehicle!");
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
