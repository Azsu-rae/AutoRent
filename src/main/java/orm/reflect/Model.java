package orm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import util.BugDetectedException;
import orm.annotate.Constraints;

import static util.CaseConverter.camelToPascal;
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

    private static final Map<Class<? extends Table<?>>, Model<?>> pool = new HashMap<>();
    public static boolean existsFor(Class<?> klass) {
        return pool.keySet().contains(klass);
    }

    private static final Map<String, Model<?>> byName = new HashMap<>();
    public static Model<?> byName(String name) {
        return byName.get(name);
    }

    private static final Map<String, Model<?>> byCollectionName = new HashMap<>();
    public static Model<?> byCollectionName(String collectionName) {
        return byCollectionName.get(collectionName);
    }

    // --------------------------------------------------------------------------------
    // CACHING LOGIC

    public static void touch() {
        /* no-op, just triggers JVM loading */
    }

    static {
        buildCache();
    }

    private static final String qualifiedPackageName = "model.";

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
        if (pool.putIfAbsent(modelKlass, model)                                  != null
            || byCollectionName.putIfAbsent(model.collectionName, model)    != null
            || byName.putIfAbsent(model.name, model)                        != null) {

            throw new IllegalStateException("You can't create the same model twice!");
        }
    }

    // About
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

    @SuppressWarnings("unchecked")
    public static <T extends Table<T>> Model<T> of(Class<T> modelClass) {
        return (Model<T>) pool.get(modelClass);
    }

    public static Model<?> ofWildcard(Class<?> modelClass) {
        if (Table.class.isAssignableFrom(modelClass)) {
            return pool.get(modelClass);
        } throw new BugDetectedException("That's not a model!");
    }

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

    @Override
    public Model<T> model() {
        return this;
    }

    @Override
    public List<Field> components() {
        return List.of(fields.all);
    }

    @Override
    public Method setter(Field field) throws NoSuchMethodException {
        return klass.getDeclaredMethod("set" + camelToPascal(field.getName()), field.getType());
    }

    @Override
    public Method getter(Field field) throws NoSuchMethodException {
        return klass.getDeclaredMethod("get" + camelToPascal(field.getName()));
    }

    public boolean hasSetter(Field field) {
        try {
            setter(field);
        } catch (NoSuchMethodException e) {
            return false;
        }

        return true;
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

        @Override
        public Model<T> model() {
            return Model.this;
        }

        @Override
        public Method getter(RecordComponent component) throws NoSuchMethodException {
            return component.getAccessor();
        }

        @Override
        public Method setter(RecordComponent component) throws NoSuchMethodException {
            return null;
        }

        @Override
        public List<RecordComponent> components() {
            return List.of(modelKlass.getRecordComponents());
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

        Fields(Model<?> model) {

            // Filtering static fields
            List<Field> filteredModelFields = new ArrayList<>();
            for (var field : model.klass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPrivate(field.getModifiers())) {
                    filteredModelFields.add(field);
                }
            }
            var modelFields = filteredModelFields.toArray(Field[]::new);

            all = new Field[modelFields.length + 1];
            try {
                all[0] = Table.class.getDeclaredField("id");
            } catch (NoSuchFieldException _) {
                throw new BugDetectedException("-___________________-");
            }

            for (int i = 0; i < modelFields.length; i++) {
                all[i + 1] = modelFields[i];
            }

            bounded = new ArrayList<String>();
            discrete = new ArrayList<String>();
            modifiable = new ArrayList<String>();
            byName = new HashMap<String, Field>();

            for (int i = 0; i < all.length; i++) {

                var name = all[i].getName();
                var constraints = all[i].getAnnotation(Constraints.class);
                if (constraints == null) {
                    throw new BugDetectedException("No 'Constraints' annotation found for " + all[i]);
                }

                if (constraints.bounded() || constraints.lowerBound()) {
                    bounded.add(name);
                } else {
                    discrete.add(name);
                }

                if (model.hasSetter(all[i])) {
                    modifiable.add(name);
                }

                byName.put(all[i].getName(), all[i]);
            }
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

        public List<Column> haveConstraint(Function<Constraints, Boolean> check) {
            return columns.stream()
                    .filter(c -> check.apply(c.constraints))
                    .toList();
        }

        public List<Column> ofType(Class<?> type) {
            return columns.stream()
                    .filter(c -> c.type.equals(type))
                    .toList();
        }

        // visible is relative to the user of the model. Sometimes the user only passes
        // strings that are then parsed to a LocalDate for example.
        public Class<?> visibleTypeOf(Field field) {
            Class<?> type = field.getType();
            if (type.equals(LocalDate.class)) {
                type = String.class;
            } return type;
        }

        public boolean has(String name) {
            return byName.containsKey(name);
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
    }
}
