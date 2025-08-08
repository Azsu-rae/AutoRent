package orm.model;

import orm.Table;

import utilities.Pair;
import utilities.Column;

import java.time.LocalDate;
import java.util.Vector;

public class Vehicle extends Table {

    @Column(type = "DECIMAL", nullable = false, bounded = true)
    private Double pricePerDay;
    @Column(type = "TEXT", nullable = false)
    private String state;
    @Column(type = "DATE", nullable = false, bounded = true)
    private LocalDate maintenanceDate;

    @Column(type = "TEXT")
    private String brand;
    @Column(type = "TEXT")
    private String model;
    @Column(type = "INTEGER", bounded = true)
    private Integer year;
    @Column(type = "TEXT")
    private String vehiculeType;
    @Column(type = "TEXT")
    private String fuelType;

    public Vehicle() {}

    public Vehicle(String pricePerDay, String state, String maintenanceDate, String year, String brand, String model, String vehiculeType, String fuelType) {

        this(Double.parseDouble(pricePerDay), state, maintenanceDate, Integer.parseInt(year), brand, model, vehiculeType, fuelType);
    }

    public Vehicle(Double pricePerDay, String state, String maintenanceDate, Integer year, String brand, String model, String vehiculeType, String fuelType) {

        this.pricePerDay = pricePerDay;
        this.state = state;
        this.maintenanceDate = stringToDate(maintenanceDate);
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vehiculeType = vehiculeType;
        this.fuelType = fuelType;
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

        this.vehiculeType = s;
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

        return this.vehiculeType;
    }

    public String getFuelType() {

        return this.fuelType;
    }
}
