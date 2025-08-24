package orm.util;

import orm.Table;
import orm.model.Client;
import orm.model.Payment;
import orm.model.Reservation;
import orm.model.Return;
import orm.model.User;
import orm.model.Vehicle;
import orm.util.Constraints;

import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.ArrayList;

import java.util.function.Function;

import java.lang.reflect.*;

public class Reflection {

    static String qualifiedPackageName = "orm.model.";
    static {
        new Client();
        new Vehicle();
        new Reservation();
        new Return();
        new Payment();
        new User();
    }

    Table tuple;
    Field[] fields;
    Class<?> model;
    String modelName;

    public Reflection(String modelName) {
        this(getModelInstance(modelName));
    }

    public Reflection(Table tuple) {

        this.tuple = tuple;
        this.model = tuple.getClass();
        this.modelName = tuple.getClass().getSimpleName();

        Field[] modelFields = processFields(field -> {
            if (field.isAnnotationPresent(Constraints.class)) {
                return field;
            } else {
                return null;
            }
        }, model.getDeclaredFields()).toArray(Field[]::new);

        Field[] effectiveFields = new Field[modelFields.length+1];
        effectiveFields[0] = getField(Table.class, "id");

        for (int i=0;i<modelFields.length;i++) {
            effectiveFields[i+1] = modelFields[i];
        }

        this.fields = effectiveFields;
    }

    // Method to get a model's instance

    public static Table getModelInstance(String className) {
        return getModelInstance(className, new Object[0]);
    }

    public static Table getModelInstance(String className, Object[] args) {
        return (Table) getInstance(getConstructor(getModel(className), objectArrayToTypeArray(args)), args);
    }

    private static Class<?>[] objectArrayToTypeArray(Object[] objs) {

        Class<?>[] types = new Class<?>[objs.length];
        for (int i=0;i<objs.length;i++) {
            types[i] = objs[i].getClass();
        }

        return types;
    }

    // Cascading deletion

    public boolean cascadeDeletion() {

        boolean success = true;
        for (String referencerName : getReferencerNames()) {

            if (!Table.isSearchable(referencerName)) {
                continue;
            }

            for (Table criteria : getReferencerCriterias(referencerName)) {
                for (Table referencer : Table.search(criteria)) {
                    for (Field relevantField : getReferencingFieldsFrom(referencerName)) {

                        if (relevantField.getAnnotation(Constraints.class).nullable()) {
                            success = success && setFieldValue(referencer, relevantField, null).edit();
                        } else {
                            success = success && referencer.delete();
                        }
                    }
                }
            }
        }

        return success;
    }

    private String[] getReferencerNames() {

        List<String> referencerNames = new ArrayList<>();
        Class<?> thisModel = getModel();

        for (Class<?> model : Table.getModels()) {

            Field[] fields = model.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(thisModel)) {
                    referencerNames.add(model.getSimpleName());
                }
            }
        }

        return referencerNames.toArray(String[]::new);
    }

    private Vector<Table> getReferencerCriterias(String referencerName) {

        Field idField = getField(Table.class, "id");
        Vector<Table> referencerCriterias = new Vector<>();
        Field[] relevantFields = getReferencingFieldsFrom(referencerName);

        for (Field relevantField : relevantFields) {

            Table instanceOfMyself = getModelInstance(modelName);
            setFieldValue(instanceOfMyself, idField, tuple.getId());

            Table referencer = getModelInstance(referencerName);
            setFieldValue(referencer, relevantField, instanceOfMyself);

            referencerCriterias.add(referencer);
        }

        return referencerCriterias;
    }

    private Field[] getReferencingFieldsFrom(String modelName) {

        return processFields(field -> {
            if (field.getType().equals(model)) {
                return field;
            }
            return null;
        }, getModel(modelName).getDeclaredFields()).toArray(Field[]::new);
    }

    // Attributes methods

    public int getFieldsNumber() {
        return fields.length;
    }

    public Class<?> getFieldClass(int i) {
        return getFieldClasses()[i];
    }

    public Class<?> getFieldClass(String name) {
        return getField(name).getType();
    }

    public Object getFieldValue(int i) {
        return getFieldValue(fields[i]);
    }

    public Object getFieldValue(String name) {
        return getFieldValue(getField(name));
    }

    public Table setFieldValue(int i, Object value) {
        setFieldValue(fields[i], value);
        return tuple;
    }

    public Table setFieldValue(String name, Object value) {
        setFieldValue(getField(name), value);
        return tuple;
    }

    public List<String> getDiscreteFieldNames() {

        var boundeds = getBoundedFieldNames(); 
        return processFields(field -> {
            if (!boundeds.contains(field.getName())) {
                return field.getName();
            }
            return null;
        });
    }

    public List<String> getBoundedFieldNames() {
        return processFields(field -> {
            Constraints col = field.getAnnotation(Constraints.class);
            if (col.bounded() || col.lowerBound()) {
                return field.getName();
            }
            return null;
        });
    }

    public Class<?>[] getFieldClasses() {
        return processFields(Field::getType).toArray(Class<?>[]::new);
    }

    public String[] getFieldNames() {
        return processFields(Field::getName).toArray(String[]::new);
    }

    public Constraints[] getFieldConstraints() {
        return processFields(field -> field.getAnnotation(Constraints.class)).toArray(Constraints[]::new);
    }

    private <R> List<R> processFields(Function<Field,R> func) {
        return processFields(func, fields);
    }

    private <R> List<R> processFields(Function<Field,R> func, Field[] fields) {

        List<R> attributes = new ArrayList<>();
        for (Field field : fields) {
            Optional
                .ofNullable(func.apply(field))
                .ifPresent(attributes::add);
        }

        return attributes;
    }

    // Default-valued instance methods

    private Field getField(String name) {
        return getField(model, name);
    }

    private Table setFieldValue(Field field, Object value) {
        return setFieldValue(tuple, field, value);
    }

    private Object getFieldValue(Field field) {
        return getFieldValue(tuple, field);
    }

    private Class<?> getModel() {
        return model;
    }

    // Primary methods

    static private Table setFieldValue(Table tuple, Field field, Object value) {

        try {
            field.setAccessible(true);
            field.set(tuple, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return tuple;
    }

    static private Object getFieldValue(Table tuple, Field field) {

        try {
            field.setAccessible(true);
            return field.get(tuple);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    static private Field getField(Class<?> model, String fieldName) {

        Field field = null;

        try {
            field = model.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return field;
    }

    static private Table getInstance(Constructor<?> constructor, Object[] args) {

        Table instance = null;

        try {
            instance = (Table) constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.err.println("Cause of InvocationTargetException: " + e.getCause());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return instance;
    }

    static private Constructor<?> getConstructor(Class<?> model, Class<?>[] types) {

        Constructor<?> constructor = null;

        try {
            constructor = model.getConstructor(types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return constructor;
    }

    static private Class<?> getModel(String modelName) {

        if (!Table.hasSubClass(modelName)) {
            throw new IllegalArgumentException("Invalid model name: " + modelName);
        }

        Class<?> model = null;

        try {
            model = Class.forName(qualifiedPackageName + modelName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return model;
    }
}
