package orm.model;

import orm.Table;

import orm.util.Pair;
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

    @Constraints(type = "TEXT")
    private String brand;
    @Constraints(type = "TEXT")
    private String model;
    @Constraints(type = "INTEGER", bounded = true)
    private Integer year;
    @Constraints(type = "TEXT")
    private String vehicleType;
    @Constraints(type = "TEXT")
    private String fuelType;

    public Vehicle() {}

    public Vehicle(String pricePerDay, String state, String maintenanceDate, String year, String brand, String model, String vehicleType, String fuelType) {

        this(
            Double.parseDouble(pricePerDay), 
            state, maintenanceDate, 
            Integer.parseInt(year), 
            brand, model, vehicleType, fuelType
        );
    }

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

    public static boolean isSearchable() {

        return isSearchable(new Vehicle());
    }

    public static Vector<Table> search() {

        return search(new Vehicle());
    }

    public static Vector<Table> search(String attributeName, Object lowerBound, Object upperBound) {

        return search(new Vehicle(), attributeName, lowerBound, upperBound);
    }

    public static Vector<Table> searchRanges(Vector<Pair<Object,Object>> boundedCriterias) {

        Vector<Table> tuples = new Vector<>();
        tuples.add(new Vehicle());
        return search(tuples, boundedCriterias);
    }

    public Vehicle setPricePerDay(Double d) {

        this.pricePerDay = d;
        return this;
    }

    public Vehicle setState(String s) {

        this.state = s;
        return this;
    }

    public Vehicle setMaintenanceDate(String s) {

        this.maintenanceDate = stringToDate(s);
        return this;
    }

    public Vehicle setYear(Integer s) {

        this.year = s;
        return this;
    }

    public Vehicle setBrand(String s) {

        this.brand = s;
        return this;
    }

    public Vehicle setModel(String s) {

        this.model = s;
        return this;
    }

    public Vehicle setVehicleType(String s) {

        this.vehicleType = s;
        return this;
    }

    public Vehicle setFuelType(String s) {

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
}
