
# How to use the ORM and define models

The ORM will as usual have you define models that go in the `model` package. Each model implementation `ModelClass` extends the `orm.Table` class and registers itself in its own static block as follow:

```java
@Collection("models") // the plural version of the model name. Used for the JSON collection and SQLite table names
public class ModelClass extends Table {

    static {
        ORM.Model.register(ModelClass.class);
    }

    // mandatory no-args constructor
    public ModelClass() {
        super(ModelClass.class);
    }

    // rest of the model defintion, read the text below

}
```

- Database columns are defined by
    - having the `@Constraints` annotation.
    - being private

Reflection-based read and write operations in the ORM go through the getter and setter methods; Not defining a getter/setter will result in the field being un-readable/un-writable. This can be used to control the behavior of the ORM.

Additionally, implementing the static methods below could benefit your experience using the API:

```java
isSearchable();
search();
search(String attName, Object value);
search(String boundedAttributeName, Object lowerBound, Object upperBound);
searchRanges(Vector<Range> boundedCriterias);
```
