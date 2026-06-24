
# Motivation

This is a Single-Source-of-Truth interfaces generator where you define your data models in classes and a CRUD interface is
generated using reflection.

The project includes a custom ORM and uses SQLite as a database. This is a learning project for strongly typed langages and
Object-Oriented separation of concerns. It is also my first programming project with a GUI and a database in a high-level
language like Java (I had only ever worked with C at the time).

It was overall a great learning experience.

# Dependencies

## Backend

- **sqlite-jdbc-3.50.3.0.jar:** SQLite Implementation of JDBC
- **json-20250517.jar:** To store sample data using JSON key-value pairs

## Frontend

- **jcalendar-1.4.jar** (potentially)
- **flatlaf-3.6.2.jar**

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
