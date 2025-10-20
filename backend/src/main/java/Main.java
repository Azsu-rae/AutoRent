
import orm.Table;
import orm.model.*;
import orm.Table.Range;

import java.util.Vector;

import static orm.util.Console.*;
import static orm.util.Database.*;

public class Main {

    public static void main(String[] args) {
        readSampleData();
        display();
    }

    private static void tutorial() {

        /*
         * Hello,
         *
         * This file serves as a test and a tutorial for using the backend.
         *
         * The backend is an Object-Relational Mapping (ORM) system designed to interact with
         * a SQLite database through Java objects, eliminating the need to write SQL queries manually.
         * It supports CRUD operations (Create, Read, Update, Delete), which we will explore shortly.
         *
         * Careful reviewing of this file should provide a clear understanding of how to utilize 
         * the ORM effectively.
         *
         * Hope this helps!
         *
         */

        // ------------------- THE CONSTRUCTORS ---------------------------

        // You can, of course, create an object of what table you want. For example:

        Client ilyas = new Client("Ilyas", "Ait-Ameur", "aitameurmedilyas@gmail.com", "0560308452", "DKSF23");

        // creates a client. It is not, however, immediately inputed in the database. For that you'll have to
        // add it using the method:

        boolean success = ilyas.add() >= 1;

        if (success) {
            System.out.println("\nThe object 'ilyas' was successfully inserted in the DB!");
        }

        // And as you can see, the method returns the number affected rows in the operation.
        //
        // Note:
        //  ->  this mehtod will also create the database file and the clients SQLite table automatically.
        //  ->  for the clients SQLite table, all the object's attributes have to be != null in order to
        //      be valid for insertion. Similarily, All models check for validity before insertion.
        //      You can check for validity yourself using 'ilyas.isValid()'.
        //      The method checks wether there are any non-nullable attributes that are, well, null.
        //
        //      In case the tuple is invalid for insertion, the .add() method fails and returns 0.

        // Knowing that memorizing the order of the attributes in a constructor has to be tiring, and
        // in favor of convenience, the setters for each model return the object itself allowing for 
        // method chaining. For example:

        var c = new Client()
            .setName("Hicham")
            .setSurname("Gaceb")
            .setEmail("hichamgaceb@gmail.com")
            .setPhoneNumber("05483729493")
            .setDrivingLicence("KSDU343")
            .add(); // finally adding it to the DB

        // this approach is more readable and should allow the user to create an object without having to
        // write too many 'null' values in a constructor.

        // For tables like vehicles that have non-string attributes,

        Vehicle v = new Vehicle(29.99, "Available", "2024-12-01", 2022, "Toyota", "Corolla", "Sedan", "Gasoline");

        // For tables that have foreign keys, you have to pass the foreing key's java instantiation.
        // if the sent tuples don't have an ID field, the construction will fail and throw an exception.
        //

        // here c and v weren't retrieved from the database and therefore do not have an ID. Thus,
        //
        //          Reservation r = new Reservation(c, v, "2022-01-01", "2022-02-01");
        //
        // will throw an IllegalArgumentException because c and v don't have an ID. If you wish
        // to create objects with tuples make sure you retrieve those from the database.
        //
        // you can check if the tuple is valid and has an ID field using Table.isTuple(<your tuple>)

        // ---------------------- THE SEARCH METHOD -------------------------------------

        // Reading through the models' tables was made as easy as possible. The search methods takes in two
        // kind of arguments:
        //
        //  - A tuple ('tuple' refers to a SQLite table line, but in this case it means a model's instance)
        //  \-> for discrete-valued attributes (like a brand in the case a vehicle or a client's name)
        //
        //  - A range that can be represented in a Pair-type variable.
        //  \-> for continuous-valued attriubtes  (like the price per day of a car or a date range)
        //
        // Both of these can be in vectors when the criterias's values within a same attribute are multiple
        // (for discrete criterias) or when we have multiple range-type attributes
        //
        // The tuples can be stored in any Table-inherited references.

        Vector<Table> clients = Client.search(); // returns all clients (only two at this point)
        print(clients, "Clients");

        // Let's create some vehicles and input them in the DB to see this:
        new Vehicle(35.50, "Rented", "2023-10-15", 2020, "Ford", "Fiesta", "Hatchback", "Diesel").add();
        new Vehicle(40.00, "Unavailable", "2024-05-10", 2021, "Tesla", "Model3", "Electric", "Electric").add();
        new Vehicle(25.75, "Available", "2024-11-20", 2018, "Honda", "Civic", "Coupe", "Hybrid").add();
        new Vehicle(50.00, "Rented", "2023-08-30", 2023, "BMW", "X5", "SUV", "Gasoline").add();
        new Vehicle(18.99, "Available", "2024-03-25", 2017, "Volkswagen", "Polo", "Hatchback", "Diesel").add();
        new Vehicle(22.50, "Unavailable", "2024-07-12", 2019, "Hyundai", "Elantra", "Sedan", "Gasoline").add();
        new Vehicle(60.00, "Available", "2024-01-18", 2024, "Audi", "Q7", "SUV", "Gasoline").add();
        new Vehicle(28.00, "Rented", "2023-09-05", 2020, "Chevrolet", "Malibu", "Sedan", "Gasoline").add();
        new Vehicle(32.75, "Available", "2024-06-30", 2019, "Nissan", "Altima", "Sedan", "Gasoline").add();

        // Let's do some filtering!

        // returns all vehicles
        Vector<Table> vehicles = Vehicle.search();
        print(vehicles, "Vehicles");

        // returns all sedans
        Vector<Table> sedans = Vehicle.search("vehicleType", "Sedan"); 
        print(sedans, "Sedans");

        // the name of the criteria must be passed to the Pair<> when creating a filter,
        // it has to be the exact same as the class attribute.
        //
        // Passing an incorrect attribute name will throw an exception, so be careful.
        // Of course the range values's type has to be correct. For example
        //
        //              Vehicle.search("year", 2020.0, 2024.0)
        //
        // will throw an exception as 'year' is an integer

        // filter the vehicles by year:
        Vector<Table> newVehicles = Vehicle.search("year", 2020, 2024);
        print(newVehicles, "New Vehicles (2020 - 2024)");

        // new Sedans
        Vehicle sedanFilter = new Vehicle().setVehicleType("Sedan");
        Vector<Table> newSedans = Vehicle.search(sedanFilter, "year", 2020, 2024); 
        print(newSedans, "New Sedans");

        // Sedans from Nissan
        Vector<Table> sedansFromNissan = Vehicle.search(
                new Vehicle()
                    .setVehicleType("Sedan")
                    .setBrand("Nissan")
        );
        print(sedansFromNissan, "Sedans from Nissan");

        // BMWs and Toyotas
        // You send a Vector of tuples for multiple discrete search criterias,
        Vector<Vehicle> BMWsToyotasCriteria = new Vector<>();
        BMWsToyotasCriteria.add(new Vehicle().setBrand("Toyota"));
        BMWsToyotasCriteria.add(new Vehicle().setBrand("BMW"));
        Vector<Table> bt = Vehicle.search(BMWsToyotasCriteria);
        print(bt, "BMWs or Toyotas");

        // NOTE: passing an empty array will throw an exception. If you don't need to filter
        // and want all tuples use search()

        // Recent cheap vehicles (different method for multiple ranges: searchRanges())
        Vector<Range> newCheapVehicleCriteria = new Vector<>();
        newCheapVehicleCriteria.add(new Range("year", 2020,2024));
        newCheapVehicleCriteria.add(new Range("pricePerDay", 30.0, 40.0));
        Vector<Table> newCheapVehicles = Vehicle.searchRanges(newCheapVehicleCriteria);
        print(newCheapVehicles, "New Cheap Vehicles");

        // And you of course also have Cheap and new Toyotas or BMWs! (Not sure)
        Vector<Table> dream = Vehicle.search(BMWsToyotasCriteria, newCheapVehicleCriteria);
        print(dream, "The Dream");

        // If you want to perform a search without having to type in the name of class,
        // (for more general work) it is possible to use Table.search() but then you would
        // need to provide either: 
        //  ->  at least one instance of the model you are searching for. (e.g. Table.search(new Client())
        //      or any varient as long as you provide at least one instance of what you are searching for
        //  ->  The name of model to search in (e.g. Table.search("Reservation")) if there are no criterias
        //
        //      NOTE: Passing a wrong class name will throw an IllegalArgumentException

        // SPECIAL CASE 1: RESERVATION DATE RANGES 

        // For searching through date ranges, we usualy use the ranges:
        Vector<Table> maintenancesIn2024 = Vehicle.search("maintenanceDate", "2024-01-01", "2024-12-31");
        print(maintenancesIn2024, "All 2024 Vehicles Maintenances");

        // But in the case of Reservations, since a reservation's period has two dates, the given range 
        // would include all the reservations whose ranges intersect with the given one. 

        // Let's input some Reservations in the DB, and then print them to the console

        // Since we need clients to input reservations, we'll start with them

        // Let's start by deleting everything we have
        clients = Client.search();
        for (Table client : clients) {
            if (client.delete() >= 1) {}
            // deletes from the database using the ID
            // the deletion will be cascaded to all the models that reference
            // a Client object. It will be set to null in case the reference is nullable, deleted otherwise
        }

        // Now let's add some good examples
        new Client("John", "Doe", "Ajohn.doe@example.com", "1234567890", "BC12345").add();
        new Client("Jane", "Smith", "jane.smith@example.com", "9876543210", "YZ67890").add();
        new Client("Alice", "Brown", "alice.brown@example.com", "4561237890", "LMN45678").add();
        new Client("Bob", "White", "bob.white@example.com", "7894561230", "PQR12345").add();
        new Client("Charlie", "Black", "charlie.black@example.com", "3216549870", "mGHI98765").add();
        new Client("Emily", "Green", "emily.green@example.com", "6549873210", "TUV65432").add();
        new Client("David", "Gray", "david.gray@example.com", "7891234560", "DEF32145").add();
        new Client("Sophia", "Blue", "sophia.blue@example.com", "1597534860", "KLM78912").add();
        new Client("Lucas", "Orange", "lucas.orange@example.com", "9517534860", "NOP85246").add();
        new Client("Olivia", "Yellow", "olivia.yellow@example.com", "3579514860", "QRS74125").add();

        // Let's get them (we didn't do this with the previously created clients because they don't have an ID,
        // which is given by the DB)
        clients = Client.search();

        Vector<Reservation> createdReservations = new Vector<>();
        createdReservations.add(new Reservation().setStartDate("2024-12-20").setEndDate("2025-01-08"));
        createdReservations.add(new Reservation().setStartDate("2024-12-21").setEndDate("2025-01-09"));
        createdReservations.add(new Reservation().setStartDate("2024-12-22").setEndDate("2025-01-10"));
        createdReservations.add(new Reservation().setStartDate("2024-12-23").setEndDate("2025-01-11"));
        createdReservations.add(new Reservation().setStartDate("2024-12-24").setEndDate("2025-01-12"));

        for (int i=0;i<createdReservations.size();i++) {
            Reservation r = createdReservations.elementAt(i).setVehicle((Vehicle)vehicles.elementAt(i)).setClient((Client) clients.elementAt(i));
            if (r.add() < 1) {
                error("ISSUE!");
            }
        }

        Vector<Table> reservations = Reservation.search();
        print(reservations, "Reservations");

        // Now to get to the point, we'll try a search

        // returns all reservations that intersects with the given intervall (Always pass the name of 
        // the lowerBound when looking for intersections)
        Vector<Table> intersectedPeriods = Reservation.search("startDate", "2024-12-15", "2024-12-25");

        Vector<Table> disjointReservations = new Vector<>();
        for (Table tuple : reservations) {

            boolean good = true;
            for (Table bad : intersectedPeriods) {
                if (tuple.equals(bad)) { // compares using the ID
                    good = false;
                }
            }

            if (good) {
                disjointReservations.add(tuple);
            }
        }

        print(disjointReservations, "Reservations that don't coinside with the period 2024-12-15, 2024-12-25");

        // SPECIAL CASE 2: CLIENT AND USER'S NAMES

        // Normally, doing search is a discrete attribute would simply compare them, but it's different for the Client 
        // and  User table.

        // Case insensitive, check for name and surname.
        Vector<Table> clientsWhoseNameStartWithJ = Client.search(new Client().setName("j"));
        print(clientsWhoseNameStartWithJ, "Clients Whose Name Start With 'j'");

        // EDITING:

        // For example, let's set all the client's whose name start with 'j' to have the same name and surname:
        for (Table client : clientsWhoseNameStartWithJ) {
            Client curr = (Client) client;
            curr.setName("Gaceb");
            curr.setSurname("Hicham");
            curr.edit(); // uses the attributes to update the tuple that has the object's ID in the DB
                         // returns a success value in a boolean
        }

        /* 
         * We didn't really talk about the other 3 classes left, but they're workings are similar to what we saw.
         *
         *  - User is a like Client
         *  - Return and Payment are like Reservation
         *
         *  All that is left is to check the attributes and constructors of each class to know and that's it!
         *
         */
    }
}
