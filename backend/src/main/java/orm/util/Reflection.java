package orm.util;

import orm.Table;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.HashMap;

import static orm.util.Console.*;

import java.lang.reflect.*;
import java.time.LocalDate;

public class Reflection {

    static String qualifiedPackageName = "orm.model.";
    public static void loadModels(String[] modelNames) {
        for (String name : modelNames) {
            try {
                Class.forName(qualifiedPackageName + name);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Wrong model names initializers!");
            }
        }
    }

    private Table tuple;
    public FieldUtils fields;

    public Reflection(String modelName) {
        this(getModelInstance(modelName));
    }

    public Reflection(Table tuple) {
        this.tuple = tuple;
        this.fields = new FieldUtils();
    }

    // Creating a model instance

    public static Table getModelInstance(String modelName) {
        return getModelInstance(modelName, new Object[0]);
    }

    public static Table getModelInstance(String modelName, Object[] args) {
        return (Table) getInstance(getConstructor(getModel(modelName), objectArrayToTypeArray(args)), args);
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
                            success = success && setFieldValue(referencer, relevantField, null).edit() >= 1;
                        } else {
                            success = success && referencer.delete() >= 1;
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
        } return referencingFields;
    }

    // Default-valued instance methods

    private Table setFieldValue(Field field, Object value) {
        return setFieldValue(tuple, field, value);
    }

    private Object getFieldValue(Field field) {
        return getFieldValue(tuple, field);
    }

    // Primary methods

    static private Method getSetter(Table tuple, String attribute) {
        try {
            String method = "set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
            Class<?> attType = tuple.reflect.fields.visibleTypeOf(attribute);
            return tuple.getClass().getDeclaredMethod(method, attType);
        } catch (NoSuchMethodException e) {
            error(e);
        } return null;
    }

    static private Object invoke(Method method, Table tuple, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(tuple, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            error(e);
        } return null;
    }

    static private Table setFieldValue(Table tuple, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(tuple, value);
        } catch (IllegalAccessException e) {
            error(e);
        } return tuple;
    }

    static private Object getFieldValue(Table tuple, Field field) {
        try {
            field.setAccessible(true);
            return field.get(tuple);
        } catch (IllegalAccessException e) {
            error(e);
        } return null;
    }

    static private Field getField(Class<?> model, String fieldName) {
        try {
            return model.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            error(e);
        } return null;
    }

    static private Table getInstance(Constructor<?> constructor, Object[] args) {
        try {
            return (Table) constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            error(e, "Cause of InvocationTargetException: %s", e.getCause());
        } catch (IllegalAccessException | InstantiationException e) {
            error(e);
        } return null;
    }

    static private Constructor<?> getConstructor(Class<?> model, Class<?>[] types) {
        try {
            return model.getConstructor(types);
        } catch (NoSuchMethodException e) {
            error(e);
        } return null;
    }

    static private Class<?> getModel(String modelName) {

        if (!Table.hasSubClass(modelName)) {
            String s = "Invalid model name: %s";
            throw new IllegalArgumentException(String.format(s, modelName));
        }

        try {
            return Class.forName(qualifiedPackageName + modelName);
        } catch (ClassNotFoundException e) {
            error(e);
        } return null;
    }

    public class FieldUtils {

        private Map<String,List<String>> modifiable = new HashMap<>();
        private Map<String,Field> fieldByName;
        private Field[] fields;

        public int count;
        public String[] names;
        public Class<?>[] types;
        public Constraints[] constraints;
        public List<String> bounded, discrete;

        private FieldUtils() {

            Field[] modelFields = tuple.getClass().getDeclaredFields();
            Field[] effectiveFields = new Field[modelFields.length+1];
            effectiveFields[0] = Reflection.getField(Table.class, "id");
            for (int i=0;i<modelFields.length;i++) {
                effectiveFields[i+1] = modelFields[i];
            } this.fields = effectiveFields;

            this.count = fields.length;
            this.names = new String[count];
            this.types = new Class<?>[count];
            this.bounded = new ArrayList<>();
            this.discrete = new ArrayList<>();
            this.fieldByName = new HashMap<>();
            this.constraints = new Constraints[count];

            for (int i=0;i<count;i++) {

                names[i] = fields[i].getName();
                types[i] = fields[i].getType();
                constraints[i] = fields[i].getAnnotation(Constraints.class);

                if (constraints[i].bounded() || constraints[i].lowerBound()) {
                    bounded.add(names[i]);
                } else {
                    discrete.add(names[i]);
                }

                fieldByName.put(fields[i].getName(), fields[i]);
            }
        }

        public List<String> modifiable() {
            return modifiable.computeIfAbsent(tuple.getClass().getSimpleName(), k -> {
                var list = new ArrayList<String>();
                for (String att : tuple.reflect.fields.names) {
                    if (getSetter(tuple, att) != null) {
                        list.add(att);
                    }
                } return list;
            });
        }

        public List<String> haveConstraint(Function<Constraints,Boolean> check) {
            var fields = new ArrayList<String>();
            for (int i=0;i<count;i++) {
                if (check.apply(constraints[i])) {
                    fields.add(names[i]);
                }
            } return fields;
        }

        public Constraints constraintsOf(String name) {
            return fieldByName.get(name).getAnnotation(Constraints.class);
        }

        public Class<?> typeOf(int i) {
            return fields[i].getType();
        }

        public Class<?> typeOf(String name) {
            return fieldByName.get(name).getType();
        }

        public Class<?> visibleTypeOf(String name) {
            Class<?> type = typeOf(name);
            if (type.equals(LocalDate.class)) {
                type = String.class;
            } return type;
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

        public Table setDiscrete(String attName, Object value) {
            if (!discrete.contains(attName)) {
                String s = "%s is not a discrete criteria!";
                throw new IllegalArgumentException(String.format(s, attName));
            } return set(attName, value);
        }

        public void callSetter(String attribute, Object value) {
            invoke(getSetter(tuple, attribute), tuple, value);
        }
    }
}
