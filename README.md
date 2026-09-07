
# Installing From Source

First, clone the repository:

```sh
git clone --depth=1 git@github.com:Azsu-rae/YADL
```

Install it into your local maven repository

```sh
mvn clean install
```

Then add it as a dependency in your project (on the same machine as the install):

```xml
<dependency>
    <groupId>asura</groupId>
    <artifactId>YADL</artifactId>
    <version>0.1</version>
</dependency>
```

Or you can find the generated `.jar` in `./target/YADL-<VERSION>.jar` but make sure to include the following dependencies in your classpath:

## Dependencies

- **sqlite-jdbc**: JDBC implementation for SQLite
- **org.json**: Lightweight, spec-compliant and dependency-free JSON implementation

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

    // getters and setter

```

- Database columns are defined by
    - having the `@Constraints` annotation.
    - being private

Reflection-based read and write operations in the ORM go through the getter and setter methods; Not defining a getter/setter will result in the field being un-readable/un-writable. This can be used to control the behavior of the ORM.
