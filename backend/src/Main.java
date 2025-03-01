
import orm.Table;
import orm.model.*;

import utilities.Pair;

import java.util.Vector;

@SuppressWarnings("rawtypes")
public class Main {

    public static void main(String[] args) {

        tutorial();
    }

    interface Printer {

        public void print(Vector<? extends Table> tuples, String s);
    }

    private static void tutorial() {

        /*
         * Hello,
         *
         * This file serves as a test and a tutorial for using the backend.
         *
         * The backend is an Object-Relational Mapping (ORM) system designed to interact with 
         * an SQLite database through Java objects, eliminating the need to write SQL queries manually. 
         * It supports CRUD operations (Create, Read, Update, Delete), which we will explore shortly.
         *
         * Carefully reviewing this file should provide a clear understanding of how to utilize 
         * the ORM effectively.
         *
         * Hope this helps!
         *
         */

        // ------------------ GENERALITIES --------------------------------

        /*
         * There are 3 packages: 'orm', 'model' and 'utilities': 
         *
         * 'utilities' only has 1 class: 'Pair'. It is used to handle ranges. 
         *
         * 'orm' is the main package. It directly contains the class 'Table' which is an abstraction of an ORM 
         * class corresponding to a SQLite table. We call such a class a Model.
         *
         * 'model' is a sub-package withing this 'orm' package. It defines 6 classes,  
         * each corresponding to a SQLite table.
         *
         * So in summary, to be able to use the database, you'll have to include: 
         *
         *  'import orm.Table;'
         *  'import orm.model.*;' 
         *  'import utilities.Pair;'
         *
         * in your code while also adding the class path './backend/bin/' (or the correct one from the directory 
         * you're from) when compilling.
         *
         */

        // ------------------- THE MODELS ---------------------------

        // You can, of course, create an object of what table you want. For example:

        Client c1 = new Client("Ilyas", "Ait-Ameur", "aitameurmedilyas@gmail.com", "0560308452", "DKSF23");

        // creates a client. It is not, however, immediately inputed in the database. For that you'll have to 
        // add it using the static method:

        boolean success = Client.add(c1);

        if (success) {
            System.out.println("\nThe object 'c1' was successfully inserted in the DB!");
        }

        // And as you can see, the method returns a boolean representing the success or not of the operation. 
        // Note that:
        //  - this mehtod will also create the database file and table for the clients automatically.
        //  - for the clients SQLite table, all the object's attributes have to be != null in order to be valid for insertion.
        //       Similarily, All models check for validity before insertion (the validity criteria depends on the model).

        // Knowing that memorizing the order of the attributes in a constructor has to be tiring, in favor of convenience, 
        // the setters for each model return the object itself allowing for method chaining. For example:
        
        Client.add(
            new Client()
                .setName("Hicham")
                .setSurname("Gaceb")
                .setEmail("hichamgaceb@gmail.com")
                .setPhoneNumber("05483729493")
                .setDrivingLicence("KSDU343")
        );

        // this approach is more readable and should allow the user to create an object without having to 
        // write too many 'null' values in a constructor.

        // ---------------------- THE SEARCH METHOD -------------------------------------

        // Reading through the models' tables was made as easy as possible. The search methods takes in two
        // kind of arguments:
        //
        //  - A tuple ('tuple' generally refers to a SQLite table line, but in this case it means a model's instance)
        //  \-> for discrete-valued attributes (like a brand in the case a vehicule or a client's name)
        //
        //  - A range that can be represented in a Pair-type variable.
        //  \-> for continuous-valued attriubtes  (like the price per day of a car or a date range)
        //
        // Both of these can be in vectors when the search criterias within a same attribute are multiple or
        // when we have multiple ranges
        //
        // The tuples can be stored in any Table-inherited references.

        // In the interest of time, printing the tuples from the search results to the console will be done using a method:
        Printer printer = (tuples, s) -> {
            // If the search method returns 'null', then it's either: 
            //  - no database or no SQLite table of the given criteria's type found
            //  - no criteria has been provided. (a criteria is a model's instance with or without attributes)
            //  ex: an empty vector or an argument with a 'null' value.
            if (tuples == null) {
                System.out.println(
                    "\n" +
                    "No database or no table found! " +
                    "At least one criteria must be provided for " + s + "!"
                );
            // If the returned vector's size is 0 then obviously there are no results
            } else if (tuples.size() == 0) {
                System.out.println("\nNo Results for " + s + "!");
            // Else we just print the results
            } else {
                System.out.println(
                    "\n" +
                    " --------------" + 
                    " Search Result for " + s + 
                    " --------------"
                );
                for (Table tuple : tuples) {
                    System.out.println("\n" + tuple);
                }
            }
        };

        // Even when there are no criterias and we want all the tuples, we have to provide a criteria ('null' will return null)
        Vector<Table> clients = Client.search(new Client()); // returns all clients
        printer.print(clients, "Clients");

        // Let's create some vehicules and input them in the DB to see this:
        Vehicule.add(new Vehicule(29.99, "Available", "2024-12-01", 2022, "Toyota", "Corolla", "Sedan", "Gasoline"));
        Vehicule.add(new Vehicule(35.50, "Rented", "2023-10-15", 2020, "Ford", "Fiesta", "Hatchback", "Diesel"));
        Vehicule.add(new Vehicule(40.00, "Unavailable", "2024-05-10", 2021, "Tesla", "Model3", "Electric", "Electric"));
        Vehicule.add(new Vehicule(25.75, "Available", "2024-11-20", 2018, "Honda", "Civic", "Coupe", "Hybrid"));
        Vehicule.add(new Vehicule(50.00, "Rented", "2023-08-30", 2023, "BMW", "X5", "SUV", "Gasoline"));
        Vehicule.add(new Vehicule(18.99, "Available", "2024-03-25", 2017, "Volkswagen", "Polo", "Hatchback", "Diesel"));
        Vehicule.add(new Vehicule(22.50, "Unavailable", "2024-07-12", 2019, "Hyundai", "Elantra", "Sedan", "Gasoline"));
        Vehicule.add(new Vehicule(60.00, "Available", "2024-01-18", 2024, "Audi", "Q7", "SUV", "Gasoline"));
        Vehicule.add(new Vehicule(28.00, "Rented", "2023-09-05", 2020, "Chevrolet", "Malibu", "Sedan", "Gasoline"));
        Vehicule.add(new Vehicule(32.75, "Available", "2024-06-30", 2019, "Nissan", "Altima", "Sedan", "Gasoline"));

        // Let's do some filtering!

        // returns all vehicules
        Vector<Table> vehicules = Vehicule.search(new Vehicule());
        printer.print(vehicules, "Vehicules");

        // returns all sedans
        Vehicule sedanFilter = new Vehicule().setVehiculeType("Sedan");
        Vector<Table> sedans = Vehicule.search(sedanFilter); 
        printer.print(sedans, "Sedans");

        // filter the vehicules by year. (automatically recognizing integers to be the year)
        Vector<Table> newVehicules = Vehicule.search(2020, 2024); 
        printer.print(newVehicules, "New Vehicules (2020 - 2024)");

        // Note that you don't need to specify the attribute that your range covers, that is because there is only one
        // continuous bounded attribute of each type in a single model, which allows the attribute to be infered automatically.

        // recent Sedans
        Vector<Table> newSedans = Vehicule.search(sedanFilter, 2020, 2024); 
        printer.print(newSedans, "New Sedans");

        // Sedans from Nissan
        Vector<Table> sedansFromNissan = Vehicule.search(
                new Vehicule()
                    .setVehiculeType("Sedan")
                    .setBrand("Nissan")
        );
        printer.print(sedansFromNissan, "Sedans from Nissan");

        // BMWs and Toyotas
        Vector<Vehicule> BMWsToyotasCriteria = new Vector<>();
        BMWsToyotasCriteria.add(new Vehicule().setBrand("Toyota"));
        BMWsToyotasCriteria.add(new Vehicule().setBrand("BMW"));
        Vector<Table> bt = Vehicule.search(BMWsToyotasCriteria);
        printer.print(bt, "BMWs or Toyotas");

        // Recent cheap vehicules (different method for multiple ranges: searchRanges())
        Vector<Pair<Object,Object>> newCheapVehiculeCriteria = new Vector<>();
        newCheapVehiculeCriteria.add(new Pair<>(2020,2024));
        newCheapVehiculeCriteria.add(new Pair<>(30.0, 40.0));
        Vector<Table> newCheapVehicules = Vehicule.searchRanges(newCheapVehiculeCriteria);
        printer.print(newCheapVehicules, "New Cheap Vehicules");

        // And you of course also have Cheap and new Toyotas or BMWs! (Not sure)
        Vector<Table> dream = Vehicule.search(BMWsToyotasCriteria, newCheapVehiculeCriteria);
        printer.print(dream, "The Dream");

        // SPECIAL CASE 1: RESERVATION DATE RANGES 

        // For searching through date ranges, we usualy use the ranges:
        Vector<Table> maintenancesIn2024 = Vehicule.search("2024-01-01", "2024-12-31");
        printer.print(maintenancesIn2024, "All 2024 Vehicules Maintenances");

        // But in the case of Reservations, since a reservation's period has two dates, the given range would include all
        // the reservations whose ranges intersect with the given one. 

        // Let's input some Reservations in the DB, and then print them to the console

        // Since we need clients to input reservations, we'll start with them

        // Let's start by deleting everything we have
        clients = Client.search(new Client());
        for (Table c : clients) {
            c.delete(); // delete the client form the DB using it's ID.
                        // returns a success value in a boolean
        }

        // Now let's add some good examples
        Client.add(new Client("John", "Doe", "Ajohn.doe@example.com", "1234567890", "BC12345"));
        Client.add(new Client("Jane", "Smith", "jane.smith@example.com", "9876543210", "YZ67890"));
        Client.add(new Client("Alice", "Brown", "alice.brown@example.com", "4561237890", "LMN45678"));
        Client.add(new Client("Bob", "White", "bob.white@example.com", "7894561230", "PQR12345"));
        Client.add(new Client("Charlie", "Black", "charlie.black@example.com", "3216549870", "mGHI98765"));
        Client.add(new Client("Emily", "Green", "emily.green@example.com", "6549873210", "TUV65432")); 
        Client.add(new Client("David", "Gray", "david.gray@example.com", "7891234560", "DEF32145")); 
        Client.add(new Client("Sophia", "Blue", "sophia.blue@example.com", "1597534860", "KLM78912")); 
        Client.add(new Client("Lucas", "Orange", "lucas.orange@example.com", "9517534860", "NOP85246")); 
        Client.add(new Client("Olivia", "Yellow", "olivia.yellow@example.com", "3579514860", "QRS74125")); 

        // Let's get them (we didn't do this with the previously created clients because they don't have an ID, which is 
        // given by the DB)
        clients = Client.search(new Client());

        Vector<Reservation> createdReservations = new Vector<>();
        createdReservations.add(new Reservation().setStartDate("2024-12-20").setEndDate("2025-01-08"));
        createdReservations.add(new Reservation().setStartDate("2024-12-21").setEndDate("2025-01-09"));
        createdReservations.add(new Reservation().setStartDate("2024-12-22").setEndDate("2025-01-10"));
        createdReservations.add(new Reservation().setStartDate("2024-12-23").setEndDate("2025-01-11"));
        createdReservations.add(new Reservation().setStartDate("2024-12-24").setEndDate("2025-01-12"));

        for (int i=0;i<5;i++) {
            Reservation.add(
                createdReservations.elementAt(i)
                    .setVehicule( (Vehicule) vehicules.elementAt(i))
                    .setClient( (Client) clients.elementAt(i))
            );
        }

        Vector<Table> reservations = Reservation.search(new Reservation());
        printer.print(reservations, "Reservations");

        // Now to get to the point, we'll try a search

        // returns all reservations that intersects with the given intervall
        Vector<Table> intersectedPeriods = Reservation.search("2024-12-15", "2024-12-25");

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

        printer.print(disjointReservations, "Reservations that don't coinside with the period 2024-12-15, 2024-12-25");

        // SPECIAL CASE 2: CLIENT AND USER'S NAMES

        // Normally, doing search is a discrete attribute would simply compare them, but it's different for the Client 
        // and  User table.

        // Case insensitive, check for name and surname.
        Vector<Table> clientsWhoseNameStartWithJ = Client.search(new Client().setName("j"));
        printer.print(clientsWhoseNameStartWithJ, "Clients Whose Name Start With 'j'");

        // EDITING:

        // For example, let's all the client's whose name start with 'j' to have the same name and surname:
        for (Table c : clientsWhoseNameStartWithJ) {
            Client curr = (Client) c;
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

        System.out.println();
    }
}
