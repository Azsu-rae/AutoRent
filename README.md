
# Quickstart

The ORM will as usual have you define models that go in the `model` package. Each model implementation `ModelClass`
extends the `orm.Table` class and should include some minimal boilerplate as follow:

```java

@Collection("students") // the plural version of the model name. Used for the JSON collection and SQLite table names
public class Student extends Table<Student> {

    static {
        Model.register(Student.class, Student.Record.class);
    }

    @Constraints(type = TEXT, nullable = false)
    private String surname;
    @Constraints(type = TEXT)
    private String name;

    @Constraints(type = TEXT)
    private String matricule;
    @Constraints(type = TEXT)
    private String email;

    public Student() {
        super(Student.class);
    }

    // Optional
    public static record Record(String surname, String name, String matricule, String email) implements Reflected<Student, RecordComponent> {
        @Override
        public Meta<Student, RecordComponent> meta() {
            return Model.of(Student.class).record;
        }
    }

```

- Database columns are defined by
    - having the `@Constraints` annotation.
    - being private

Reflection-based read and write operations in the ORM go through the getter and setter methods; Not defining a getter/setter will result in the field being un-readable/un-writable. This can be used to control the behavior of the ORM.
