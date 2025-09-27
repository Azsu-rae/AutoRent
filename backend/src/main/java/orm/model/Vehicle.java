package orm.model;

import orm.Table;

import orm.util.Constraints;

import java.time.LocalDate;
import java.util.Vector;

public class Vehicle extends Table {

    static {
        registerModel(Vehicle.class);
    }

    @Constraints(type = "DECIMAL", nullable = false, bounded = true)
    private Double pricePerDay;
    @Constraints(type = "TEXT", nullable = false)
    private String state;
    @Constraints(type = "DATE", nullable = false, bounded = true)
    private LocalDate maintenanceDate;

    @Constraints(type = "TEXT", enumerated = true)
    private String brand;
    @Constraints(type = "TEXT", enumerated = true)
    private String model;
    @Constraints(type = "INTEGER", bounded = true)
    private Integer year;
    @Constraints(type = "TEXT", enumerated = true)
    private String vehicleType;
    @Constraints(type = "TEXT", enumerated = true)
    private String fuelType;

    public Vehicle() {}

    public Vehicle(Double pricePerDay, String state, String maintenanceDate, Integer year, String brand, String model, String vehicleType, String fuelType) {
        this.pricePerDay = pricePerDay;
        this.state = state;
        this.maintenanceDate = stringToDate(maintenanceDate);
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
    }

    public Vehicle setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
        return this;
    }

    public Vehicle setState(String state) {
        this.state = state;
        return this;
    }

    public Vehicle setMaintenanceDate(String maintenanceDate) {
        this.maintenanceDate = stringToDate(maintenanceDate);
        return this;
    }

    public Vehicle setYear(Integer year) {
        this.year = year;
        return this;
    }

    public Vehicle setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public Vehicle setModel(String model) {
        this.model = model;
        return this;
    }

    public Vehicle setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    public Vehicle setFuelType(String fuelType) {
        this.fuelType = fuelType;
        return this;
    }

    public Double getPricePerDay() {
        return this.pricePerDay;
    }

    public String getState() {
        return this.state;
    }

    public String getMaintenanceDate() {
        return this.maintenanceDate.toString();
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

    public String getVehicleType() {
        return this.vehicleType;
    }

    public String getFuelType() {
        return this.fuelType;
    }

    public static boolean isSearchable() {
        return isSearchable("Vehicle");
    }

    public static Vector<Table> search() {
        return search(new Vehicle());
    }

    public static Vector<Table> search(String attName, Object value) {
        return search("Vehicle", attName, value);
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {
        return search(new Vehicle(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Range> boundedCriterias) {
        Vector<Table> tuples = new Vector<>();
        tuples.add(new Vehicle());
        return search(tuples, boundedCriterias);
    }
}
