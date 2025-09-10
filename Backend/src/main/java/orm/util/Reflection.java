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
import java.util.Map;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;

import static orm.util.Utils.*;

import java.lang.reflect.*;
import java.time.LocalDate;

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

    private Table tuple;
    public FieldUtils fields;

    public Reflection(Table tuple) {
        this.tuple = tuple;
        this.fields = new FieldUtils();
    }

    // Creating a model instance

    public static Table getModelInstance(String modelName) {
        return getModelInstance(modelName, new Object[0]);
    }

    public static Table getModelInstance(String modelName, Object[] args) {
        return (Table)
            getInstance(getConstructor(getModel(modelName), objectArrayToTypeArray(args)), args);
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

    private List<String> getReferencerNames() {

        List<String> referencerNames = new ArrayList<>();
        Class<?> thisModel = tuple.getClass();

        for (Class<?> model : Table.getModels()) {

            Field[] fields = model.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(thisModel)) {
                    referencerNames.add(model.getSimpleName());
                }
            }
        }

        return referencerNames;
    }

    private Vector<Table> getReferencerCriterias(String referencerName) {

        Field idField = getField(Table.class, "id");
        Vector<Table> referencerCriterias = new Vector<>();

        for (Field relevantField : getReferencingFieldsFrom(referencerName)) {

            Table instanceOfMyself = getModelInstance(tuple.getClass().getSimpleName());
            setFieldValue(instanceOfMyself, idField, tuple.getId());

            Table referencer = getModelInstance(referencerName);
            setFieldValue(referencer, relevantField, instanceOfMyself);

            referencerCriterias.add(referencer);
        }

        return referencerCriterias;
    }

    private List<Field> getReferencingFieldsFrom(String modelName) {

        List<Field> referencingFields = new ArrayList<>();
        for (Field field : getModel(modelName).getDeclaredFields()) {
            if (field.getType().equals(tuple.getClass())) {
                referencingFields.add(field);
            }
        }

        return referencingFields;
    }

    // Default-valued instance methods

    private Table setFieldValue(Field field, Object value) {
        return setFieldValue(tuple, field, value);
    }

    private Object getFieldValue(Field field) {
        return getFieldValue(tuple, field);
    }

    // Primary methods

    static private Method getSetter(Class<?> model, String attribute) {

        try {
            String method = "set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
            Class<?> attType = getModelInstance(model.getSimpleName()).reflect.fields.effectiveType(attribute);
            return model.getDeclaredMethod(method, attType);
        } catch (NoSuchMethodException e) {
            error(e);
        }

        return null;
    }

    static private Object invoke(Method method, Table tuple, Object... args) {

        try {
            method.setAccessible(true);
            return method.invoke(tuple, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            error(e);
        }

        return null;
    }

    static private Table setFieldValue(Table tuple, Field field, Object value) {

        try {
            field.setAccessible(true);
            field.set(tuple, value);
        } catch (IllegalAccessException e) {
            error(e);
        }

        return tuple;
    }

    static private Object getFieldValue(Table tuple, Field field) {

        try {
            field.setAccessible(true);
            return field.get(tuple);
        } catch (IllegalAccessException e) {
            error(e);
        }

        return null;
    }

    static private Field getField(Class<?> model, String fieldName) {

        Field field = null;
        try {
            field = model.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            error(e);
        }

        return field;
    }

    static private Table getInstance(Constructor<?> constructor, Object[] args) {

        Table instance = null;
        try {
            instance = (Table) constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            error(e, "Cause of InvocationTargetException: %s", e.getCause());
        } catch (IllegalAccessException | InstantiationException e) {
            error(e);
        }

        return instance;
    }

    static private Constructor<?> getConstructor(Class<?> model, Class<?>[] types) {

        Constructor<?> constructor = null;
        try {
            constructor = model.getConstructor(types);
        } catch (NoSuchMethodException e) {
            error(e);
        }

        return constructor;
    }

    static private Class<?> getModel(String modelName) {

        if (!Table.hasSubClass(modelName)) {
            String s = "Invalid model name: %s";
            throw new IllegalArgumentException(format(s, modelName));
        }

        Class<?> model = null;
        try {
            model = Class.forName(qualifiedPackageName + modelName);
        } catch (ClassNotFoundException e) {
            error(e);
        }

        return model;
    }

    public class FieldUtils {

        public Field[] fields;

        public int count;
        public String[] names;
        public Class<?>[] types;
        public Constraints[] constraints;
        public List<String> bounded, discrete;

        private Map<String,Field> fieldByName;

        private FieldUtils() {

            Field[] modelFields = tuple.getClass().getDeclaredFields();
            Field[] effectiveFields = new Field[modelFields.length+1];
            effectiveFields[0] = Reflection.getField(Table.class, "id");

            for (int i=0;i<modelFields.length;i++) {
                effectiveFields[i+1] = modelFields[i];
            }

            this.fields = effectiveFields;

            this.count = fields.length;
            this.names = new String[count];
            this.types = new Class<?>[count];

            this.constraints = new Constraints[count];
            this.fieldByName = new HashMap<>();

            this.bounded = new ArrayList<>();
            this.discrete = new ArrayList<>();

            for (int i=0;i<count;i++) {

                fieldByName.put(fields[i].getName(), fields[i]);

                names[i] = fields[i].getName();
                types[i] = fields[i].getType();
                constraints[i] = fields[i].getAnnotation(Constraints.class);

                if (constraints[i].bounded() || constraints[i].lowerBound()) {
                    bounded.add(names[i]);
                } else {
                    discrete.add(names[i]);
                }
            }
        }

        public Class<?> type(int i) {
            return fields[i].getType();
        }

        public Class<?> type(String name) {
            return fieldByName.get(name).getType();
        }

        public Class<?> effectiveType(String name) {
            Class<?> type = type(name);
            if (type.equals(LocalDate.class)) {
                type = String.class;
            }
            return type;
        }

        public Object get(int i) {
            return getFieldValue(fields[i]);
        }

        public Object get(String name) {
            return getFieldValue(fieldByName.get(name));
        }

        public Table set(int i, Object value) {
            setFieldValue(fields[i], value);
            return tuple;
        }

        public Table set(String name, Object value) {
            setFieldValue(fieldByName.get(name), value);
            return tuple;
        }

        public Table callSetter(String attribute, Object value) {
            return (Table) invoke(getSetter(tuple.getClass(), attribute), tuple, value);
        }
    }
}
