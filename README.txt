
# Motivation

This is a Single-Source-of-Truth interfaces generator where you define your data models in classes and a CRUD interface is
generated using reflection.

The project includes a custom ORM and uses SQLite as a database. This is a learning project for strongly typed langages and
Object-Oriented separation of concerns. It is also my first programming project with an interface and a database in a high-level
language like Java (I had only ever worked with C at the time).

It was overall a really great learning experience.

# Dependencies

## Backend

- sqlite-jdbc-3.50.3.0.jar: SQLite Implementation of JDBC
- json-20250517.jar: To store sample data using JSON key-value pairs

## Frontend

- jcalendar-1.4.jar (potentially)
- flatlaf-3.6.2.jar

# Documentation

## ORM model definitions

- you can have static fields in models but you can't add fields other than to-be database columns

## Frontend structure

- 1 UI Entity = 1 class = 1 file
- Encapsulation using packages
- Using interfaces as contracts to communicate between elements (log in, log out, clear event, selections, ...)

# TODOs

- `./frontend/src/main/java/gui/util/Parser.java` does too much. Extract the string/name management into a separate class
    - FieldValueMapper
    - FieldLabelFormatter

- fix the `MyDialog` implementations

- Engineer a mapping layer to map data types to their corresponding input interface

# Scratch

. Ideas
|
+-> . Add on focus coloring of the borders on the Sign In page
+-> . Dark & Light Mode
+-> . Bills
+-> . Undo/Redo
+-> . Keyboard shortcuts
+-> . Show loading states
+-> . Operation Cancelling

+-> . make the form a dialog that opens when you press a button
+-> . foreignKey picker
+-> . Different visiblity for managers and admins
+-> . Consistent & Compelete Business Logic
+-> . History
+-> . Payments & Return for reservations
+-> . Home Page Functions
|   |
|   +-> . Repports (loyal clients, popular vehicles, ...)
|
+-> . Notifications?

+-> . Use javadoc
+-> . Write Tests

+-> . Dire need for better logging

+-> . use proper password hashing
+-> . backend-level input validation (that's where it should be anyways, throw exceptions at bad args)

+-> . transaction support (critical for operations like payment + reservation)
+-> . connection pooling (will have performance issues)
+-> . caching mechanism

+-> . Client-side input validation (with good messages)

+-> . Make a loading screen

+-> . Check Parser for consistency (also check for empty vs real errors)

+-> use dependency injection or singleton pattern properly.

+-> check back parseSelectedRow (Grid:56)

+-> Pagination? (not loading everything at once)

+-> Deletion Confirmation

------------------------------- AMBIGOUS -------------------------------

+-> . Databse operations should happen in Event Dispatch Thread (EDT) thread

------------------------------- DONE -------------------------------

+-> . Memory management for interfaces
|   |
|   +-> . Grid adds itself to the static list Opts.toClears but is never removed
|   +-> . Listeners added to stuff that will stop being used (like pickers, dialogs, ...) should be removed
