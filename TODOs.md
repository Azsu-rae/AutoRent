
# TODOs

## Urgent

## Pending

### ORM

- should i eventually check for misaligned migrations????

### GUI

- Engineer a mapping layer to map data types to their corresponding input UI

## Done

- test the ORM with a new schema with a sample data seeding

- check if the generated keys mechanism works as intended

- Implement adequate logging in `Table` for sanity's sake

- make sure the `Table.add` method checks wether migrations have beed done

- Preliminary modelization of the new business purpose

- initial migration of the preliminary modelization

- `./frontend/src/main/java/gui/util/Parser.java` does too much. Extract the string/name management into a separate class
    - FieldValueMapper
    - FieldLabelFormatter

- fix the `MyDialog` implementations

- Extract non-UI element classes out of the `gui` package

- Collapse the frontend/backend separation into a single project package
