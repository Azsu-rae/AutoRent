package orm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import model.Specialty;
import util.BugDetectedException;
import orm.annotate.Constraints;

import static util.CaseConverter.*;
import static util.Log.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import orm.Table;
import orm.ORM;

import util.ModelDefinitionException;

/**
 * Cached metadata about all the models in the ORM.
 */
public class Model<T extends Table<T>> extends Meta<T,Field> {

    public static void debug() {
    }

    // --------------------------------------------------------------------------------
    // STATIC CACHE & GETTERS

    public static final DGraph dgraph;

    private static final Map<Class<? extends Table<?>>, Model<?>> pool = new HashMap<>();

    @SuppressWarnings("unchecked") // TODO: check if Table.class is allowed here
    public static <T extends Table<T>> Model<T> of(Class<T> modelClass) {
        return (Model<T>) pool.get(modelClass);
    }

    public static Model<?> ofWildcard(Class<?> modelClass) {
        if (!pool.containsKey(modelClass)) {
            throw new IllegalArgumentException(modelClass + " is not a model!");
        }
        return pool.get(modelClass);
    }

    public static boolean existsFor(Class<?> klass) {
        return pool.containsKey(klass);
    }

    public static Collection<Model<?>> all() {
        return Collections.unmodifiableCollection(pool.values());
    }

    private static final Map<String, Model<?>> byName = new HashMap<>();
    public static Model<?> ofName(String name) {
        return byName.get(name);
    }

    private static final Map<String, Model<?>> byCollectionName = new HashMap<>();
    public static Model<?> ofCollectionName(String collectionName) {
        return byCollectionName.get(collectionName);
    }

    // --------------------------------------------------------------------------------
    // INITIALIZATION & CACHING MECHANISM

    public static void touch() {
        /* no-op, just triggers JVM loading */
    }

    static {
        buildCache();
        dgraph = new DGraph();
    }

    private static final String qualifiedPackageName = "model.";

    // TODO: make sure of the ORM's proper initialization
    static public void buildCache() {
        cinit("BUILDING CACHE...");
        for (var name : ORM.getInstance().bootModelNames) {
            try {
                // just poking them to trigger JVM loading and subsequently the method below
                Class.forName(qualifiedPackageName + name);
            } catch (ClassNotFoundException e) {
                error(e.toString());
                throw new BugDetectedException("Wrong model name:" + name);
            }
        }
    }

    public static <T extends Table<T>, R extends java.lang.Record> void register(Class<T> modelKlass, Class<R> recordKlass) {

        cinit("Registering %s", modelKlass);

        var model = new Model<T>(modelKlass, recordKlass);
        if (pool.putIfAbsent(modelKlass, model)                          != null
            || byCollectionName.putIfAbsent(model.collectionName, model) != null
            || byName.putIfAbsent(model.name, model)                     != null) {

            throw new IllegalStateException("You can't create the same model twice!");
        }
    }

    // --------------------------------------------------------------------------------
    // IMPLEMENTATION

    public final String name;
    public final Class<T> klass;
    public final String collectionName;

    public final List<Column> columns;
    public final Fields fields;

    public final Record record;

    private <R extends java.lang.Record> Model(Class<T> modelKlass, Class<R> recordKlass) {

        this.klass = modelKlass;
        this.name = modelKlass.getSimpleName();
        this.collectionName = modelKlass.getAnnotation(orm.annotate.Collection.class).value();
        if (this.collectionName == null) {
            throw new ModelDefinitionException("Missing @Constraints on the " + name + " model");
        }

        this.fields = new Fields(this);
        this.columns = this.fields.asColumns();

        this.record = new Record(modelKlass, recordKlass);
    }

    @Override public String toString() {
        return name;
    }

    // equality is checked using identity since instances are pooled
    @Override public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override public Model<T> model() {
        return this;
    }

    @Override public List<Field> components() {
        return List.of(fields.all);
    }

    @Override public Method setter(Field field) throws NoSuchMethodException {
        return klass.getDeclaredMethod("set" + camelToPascal(field.getName()), Fields.visibleTypeOf(field));
    }

    @Override public Method getter(Field field) throws NoSuchMethodException {
        return klass.getDeclaredMethod("get" + camelToPascal(field.getName()));
    }

    // --------------------------------------------------------------------------------
    // METHODS

    public T createInstance() {
        try {
            return klass.getConstructor().newInstance();
        } catch (
                InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new ModelDefinitionException("All models must have an empty constructor!");
        }
    }

    public boolean hasSetter(Field field) {
        try {
            setter(field);
        } catch (NoSuchMethodException e) {
            return false;
        }

        return true;
    }

    public static void migrateAll() {
        for (var model : all()) {
            Table.migrate(model);
        }
    }

    public class Record extends Meta<T,RecordComponent> {

        private static final Map<Class<? extends Table<?>>, Model<?>.Record> pool = new HashMap<>();

        public final Class<T> modelKlass;
        public final Class<? extends java.lang.Record> recordKlass;

        private Record(Class<T> modelKlass, Class<? extends java.lang.Record> recordKlass) {
            this.modelKlass = modelKlass;
            this.recordKlass = recordKlass;
            pool.put(this.modelKlass, this);
        }

        @Override public Model<T> model() {
            return Model.this;
        }

        @Override public Method getter(RecordComponent component) throws NoSuchMethodException {
            return component.getAccessor();
        }

        @Override public Method setter(RecordComponent component) throws NoSuchMethodException {
            return null;
        }

        @Override public List<RecordComponent> components() {
            return List.of(modelKlass.getRecordComponents());
        }

        public java.lang.Record construct(Object... values) {
            try {
                return recordKlass.getConstructor(paramTypes()).newInstance(values);
            } catch (InstantiationException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                throw new BugDetectedException("Failed to construct record of %s!".formatted(modelKlass));
            }
        }

        public Class<?>[] paramTypes() {
            return components()
                          .stream()
                          .map(c -> c.getType())
                          .toArray(Class<?>[]::new);
        }
    }

    public class Fields {

        public final List<String> bounded, discrete, modifiable;
        public final Field[] all;

        private final Map<String, Field> byName;
        public Field byName(String name) {
            return byName.get(name);
        }

        private Fields(Model<?> model) {

            List<Field> filteredModelFields = new ArrayList<>();
            for (var field : model.klass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPrivate(field.getModifiers())) {
                    filteredModelFields.add(field);
                }
            }
            all = filteredModelFields.toArray(Field[]::new);

            bounded = new ArrayList<String>();
            discrete = new ArrayList<String>();
            modifiable = new ArrayList<String>();
            byName = new HashMap<String, Field>();

            for (int i = 0; i < all.length; i++) {

                var field = all[i];

                var name = field.getName();
                var constraints = field.getAnnotation(Constraints.class);
                if (constraints == null) {
                    throw new BugDetectedException("No 'Constraints' annotation found for " + field);
                }

                if (constraints.bounded() || constraints.lowerBound()) {
                    bounded.add(name);
                } else {
                    discrete.add(name);
                }

                if (model.hasSetter(field)) {
                    modifiable.add(name);
                }

                byName.put(field.getName(), field);
            }
        }

        public List<Column> haveConstraint(Function<Constraints, Boolean> check) {
            return columns.stream()
                    .filter(c -> check.apply(c.constraints))
                    .toList();
        }

        public List<Column> ofType(Class<?> type) {
            return columns.stream()
                    .filter(c -> type.isAssignableFrom(c.type))
                    .toList();
        }

        // visible is relative to the user of the model. Sometimes the user only passes
        // strings that are then parsed to a LocalDate for example.
        public static Class<?> visibleTypeOf(Field field) {
            Class<?> type = field.getType();
            if (type.equals(LocalDate.class) || type.isEnum()) {
                type = String.class;
            }
            return type;
        }

        public static String sqlName(String name, Class<?> type) {
            if (Table.class.isAssignableFrom(type)) {
                return camelToSnake(name) + "_id";
            } else {
                return camelToSnake(name);
            }
        }

        public boolean contains(String name) {
            return byName.containsKey(name);
        }

        private List<Column> asColumns() {
            return Stream.of(all)
                         .map(field -> new Column(
                                         field.getName(),
                                         field.getType(),
                                         field.getAnnotation(Constraints.class),
                                         field))
                         .toList();
        }
    }

    public static class Column {

        public final String name;
        public final Class<?> type;
        public final Constraints constraints;
        public final Field field;

        Column(String name, Class<?> type, Constraints constraints, Field field) {
            this.name = name;
            this.type = type;
            this.constraints = constraints;
            this.field = field;
        }

        public String sqlName() {
            return Model.Fields.sqlName(name, type);
        }

        public Class<?> visibleType() {
            return Model.Fields.visibleTypeOf(field);
        }
    }
}

class DGraph extends HashMap<Model<?>, List<Model<?>>> {

    Map<Model<?>, List<Model<?>>> transitiveDependencies = new HashMap<>();
    Set<Model<?>> independentModels = new HashSet<>(Model.all());

    DGraph() {
        for (var model : Model.all()) {
            for (var dependency : model.fields.ofType(Table.class)) {
                this.computeIfAbsent(model, _ -> new ArrayList<>()).add(Model.ofWildcard(dependency.type));
            }
        }
    }

    void compute() {
        for (var dependent : this.keySet()){
            transitiveDependencies(dependent);
        }
    }

    void transitiveDependencies(Model<?> vertice) {

        if (transitiveDependencies.containsKey(vertice)) {
            return;
        }

        var dependencies = new ArrayList<Model<?>>();
        dependencies.add(vertice);

        if (this.get(vertice) == null) {
            transitiveDependencies.put(vertice, dependencies);
            return;
        }

        for (var dependency : this.get(vertice)) {
            independentModels.remove(dependency);
            transitiveDependencies(dependency);
            dependencies.addAll(transitiveDependencies.get(dependency));
        }

        transitiveDependencies.put(vertice, dependencies);
    }

}
