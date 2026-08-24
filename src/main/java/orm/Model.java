package orm;

import static util.CaseConverter.camelToPascal;
import static util.Log.error;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orm.annotation.Constraints;

import util.BugDetectedException;
import util.IncompleteModelDefinitionException;

public class Model<T extends Table<T>> {

    static void touch() {
        /* no-op, just triggers JVM loading */
    }

    private static final Map<Class<? extends Table<?>>, Model<?>> pool = new HashMap<>();

    public static Map<Class<? extends Table<?>>, Model<?>> getPool() {
        return Collections.unmodifiableMap(pool);
    }

    private static final Map<String, Model<?>> byCollectionName = new HashMap<>();

    public static Map<String, Model<?>> byCollectionName() {
        return Collections.unmodifiableMap(byCollectionName);
    }

    public static boolean existsFor(Class<? extends Table<?>> klass) {
        return pool.keySet().contains(klass);
    }

    static {
        buildCache();
    }

    public static <T extends Table<T>> void register(Class<T> klass) {
        if (pool != null) {
            throw new IllegalStateException("Registration deadline has passed!");
        }

        var model = new Model<T>(klass);
        if (pool.putIfAbsent(klass, model) != null
                || byCollectionName.putIfAbsent(model.collectionName, model) != null) {

            throw new IllegalStateException("You can't create the same model twice!");
        }
    }

    private static final String qualifiedPackageName = "model.";

    static public void buildCache() {
        for (var name : ORM.instance.bootModelNames) {
            try {
                // just poking them to trigger JVM loading
                Class.forName(qualifiedPackageName + name);
            } catch (ClassNotFoundException e) {
                error(e);
                throw new BugDetectedException("Wrong model name:" + name);
            }
        }
    }

    final String collectionName;
    final List<Column> columns;
    final Class<?> record;
    final Class<T> klass;
    final Fields fields;
    final String name;

    private Model(Class<T> klass) {
        this.name = klass.getSimpleName();
        this.klass = klass;
        this.fields = new Fields(this);
        this.columns = fields.columns; // shortcut
        this.collectionName = klass.getAnnotation(orm.annotation.Collection.class).value();
        if (this.collectionName == null) {
            throw new IncompleteModelDefinitionException("Missing @Constraints on the " + name + " model");
        }

        try {
            this.record = Class.forName(klass.toString() + "$Record");
        } catch (ClassNotFoundException e) {
            throw new IncompleteModelDefinitionException("Please define `" + name + ".Record`!");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Table<T>> Model<T> get(Class<T> modelClass) {
        return (Model<T>) pool.get(modelClass);
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
            throw new IncompleteModelDefinitionException("All models must have an empty constructor!");
        }
    }

    public Method getSetter(String attribute) throws NoSuchMethodException {
        return klass.getDeclaredMethod("set" + camelToPascal(attribute), fields.visibleTypeOf(attribute));
    }

    public Method getGetter(String attribute) throws NoSuchMethodException {
        return klass.getDeclaredMethod("get" + camelToPascal(attribute));
    }

    public boolean hasSetter(String attribute) {
        try {
            getSetter(attribute);
        } catch (NoSuchMethodException e) {
            return false;
        }

        return true;
    }

    interface Record {
    }

    static class Column {

        public final String name;
        public final Class<?> type;
        public final Constraints constraints;

        Column(String name, Class<?> type, Constraints constraints) {
            this.name = name;
            this.type = type;
            this.constraints = constraints;
        }
    }
}
