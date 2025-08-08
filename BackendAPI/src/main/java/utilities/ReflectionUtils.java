package utilities;

import orm.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.ArrayList;

import java.util.function.Function;

import java.lang.reflect.*;

public class ReflectionUtils {

    static String qualifiedPackageName = "orm.model.";

    String className;
    Table tuple;

    public ReflectionUtils(Table tuple) {

        this.tuple = tuple;
        this.className = tuple.getClass().getSimpleName();
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

    // Attributes management methods

    public int getAttributesNumber() {

        return getFields().length;
    }

    public Class<?> getAttributeClass(int i) {

        return getFieldClasses()[i];
    }

    public Object getAttribute(int i) {

        return getFieldValue(getFields()[i]);
    }

    public void setAttribute(int i, Object value) {

        setFieldValue(getFields()[i], value);
    }

    // Field management methods

    public void cascadeDeletion() {

        for (String referencerName : getReferencerNames()) {

            for (Table referencer : Table.search(getReferencerCriterias(referencerName))) {
                for (Field relevantField : getReferencingFieldsFrom(getModel(referencerName))) {

                    if (relevantField.getAnnotation(Column.class).nullable()) {
                        setFieldValue(referencer, relevantField, null);
                    } else {
                        referencer.delete();
                    }
                }
            }
        }
    }

    private Vector<Table> getReferencerCriterias(String referencerName) {

        Field idField = getField(Table.class, "id");
        Vector<Table> referencerCriterias = new Vector<>();
        Field[] relevantFields = getReferencingFieldsFrom(getModel(referencerName));
        for (Field relevantField : relevantFields) {

            Table referencedClassInstance = getModelInstance(tuple.getClass().getSimpleName());
            setFieldValue(referencedClassInstance, idField, tuple.getId());

            Table referencer = getModelInstance(referencerName);
            setFieldValue(referencer, relevantField, referencedClassInstance);

            referencerCriterias.add(referencer);
        }

        return referencerCriterias;
    }
    
    private String[] getReferencerNames() {

        List<String> referencerNames = new ArrayList<>();
        Class<?> model = getModel();

        for (String className : Table.info.subClasses) {

            Field[] fields = getModel(className).getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(model)) {
                    referencerNames.add(className);
                }
            }
        }

        return referencerNames.toArray(String[]::new);
    }

    private Field[] getReferencingFieldsFrom(Class<?> model) {

        return getFieldAttributes(field -> {
            if (field.getType().equals(tuple.getClass())) {
                return field;
            }
            return null;
        }, model.getDeclaredFields()).toArray(Field[]::new);
    }

    public String[] getBoundedFieldNames() {

        return getFieldAttributes(field -> {
            Column col = field.getAnnotation(Column.class);
            if (col.bounded() || col.lowerBound()) {
                return field.getName();
            }
            return null;
        }).toArray(String[]::new);
    }

    public Class<?>[] getFieldClasses() {

        return getFieldAttributes(Field::getType).toArray(Class<?>[]::new);
    }

    public String[] getFieldNames() {

        return getFieldAttributes(Field::getName).toArray(String[]::new);
    }

    public Column[] getFieldColumns() {
 
        return getFieldAttributes(field -> field.getAnnotation(Column.class)).toArray(Column[]::new);
    }

    private <R> List<R> getFieldAttributes(Function<Field,R> func) {

        return getFieldAttributes(func, getFields());
    }

    private <R> List<R> getFieldAttributes(Function<Field,R> func, Field[] fields) {

        List<R> attributes = new ArrayList<>();
        for (Field field : fields) {
            Optional
                .ofNullable(func.apply(field))
                .ifPresent(attributes::add);
        }

        return attributes;
    }

    private Field[] getFields() {

        Field[] modelFields = getModel().getDeclaredFields();
        Field[] effectiveFields = new Field[modelFields.length+1];
        effectiveFields[0] = getField(Table.class, "id");

        for (int i=0;i<modelFields.length;i++) {
            effectiveFields[i+1] = modelFields[i];
        }

        return effectiveFields;
    }

    // Default-valued instance methods

    private void setFieldValue(Field field, Object value) {

        setFieldValue(tuple, field, value);
    }

    private Object getFieldValue(Field field) {

        return getFieldValue(tuple, field);
    }

    private Class<?> getModel() {

        return getModel(className);
    }

    // Primary methods

    static private void setFieldValue(Table tuple, Field field, Object value) {

        try {
            field.setAccessible(true);
            field.set(tuple, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

        if (!Arrays.asList(Table.info.subClasses).contains(modelName)) {
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
