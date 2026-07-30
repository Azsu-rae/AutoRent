

# TODOs

## Pending

### ORM

- make sure the `Table.add` method checks wether migrations have beed done
- test the ORM with a new schema with a sample data seeding

- should i eventually check for misaligned migrations????
- I have to rethink the exceptions thrown and logging mechanisms

### GUI

- Engineer a mapping layer to map data types to their corresponding input UI

## Done

- Preliminary modelization of the new business purpose

- initial migration of the preliminary modelization

- `./frontend/src/main/java/gui/util/Parser.java` does too much. Extract the string/name management into a separate class
    - FieldValueMapper
    - FieldLabelFormatter

- fix the `MyDialog` implementations

- Extract non-UI element classes out of the `gui` package

- Collapse the frontend/backend separation into a single project package
