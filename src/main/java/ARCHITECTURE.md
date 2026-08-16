
# Packages

**model**

each model implementation `ModelClass` must:

- extend the `Table` class
- register itself:

```java
    static {
        registerModel(ModelClass.class);
    }
```

- you can have static fields in models but you can't add fields other than to-be database columns
- impelement getters and setters for each field (to implement business logic details)
- all fields must be private

- use the `@Constraints` annotation for it's non-static fields
- use the `@Collection` annotation to set the plural version of it's name

- implement the static methods
    - `isSearchable()`
    - `search()`
    - `search(String attName, Object value)`
    - `search(String boundedAttributeName, Object lowerBound, Object upperBound)`
    - `searchRanges(Vector<Range> boundedCriterias)`
  for API ease of use

**orm**

A simple ORM using reflection and annotations. The structure is pretty straightforward:

```
orm
├── annotation
│   ├── Collection.java
│   └── Constraints.java
├── DataMapper.java
├── Reflection.java
├── SQLiteQueryConstructor.java
└── Table.java
```
