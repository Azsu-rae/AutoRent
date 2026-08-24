package orm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import orm.annotation.Constraints;
import util.BugDetectedException;

import orm.Model.Column;

public class Fields {

    public final List<String> bounded, discrete, modifiable;
    public final Map<String, Field> byName;
    public final List<Column> columns;

    Fields(Model<?> model) {

        // Filtering static fields
        List<Field> filteredModelFields = new ArrayList<>();
        for (var field : model.klass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPrivate(field.getModifiers())) {
                filteredModelFields.add(field);
            }
        }
        var modelFields = filteredModelFields.toArray(Field[]::new);

        Field[] fields = new Field[modelFields.length + 1];
        try {
            fields[0] = Table.class.getDeclaredField("id");
        } catch (NoSuchFieldException _) {
            throw new BugDetectedException("Stop right there what the fuck");
        }

        for (int i = 0; i < modelFields.length; i++) {
            fields[i + 1] = modelFields[i];
        }

        var bounded = new ArrayList<String>();
        var discrete = new ArrayList<String>();
        var modifiable = new ArrayList<String>();
        var fieldByName = new HashMap<String, Field>();
        var columns = new ArrayList<Column>();

        for (int i = 0; i < fields.length; i++) {

            var name = fields[i].getName();
            var constraints = fields[i].getAnnotation(Constraints.class);
            if (constraints == null) {
                throw new BugDetectedException("No 'Constraints' annotation found for " + fields[i]);
            }

            columns.add(new Column(name, fields[i].getType(), constraints));

            if (constraints.bounded() || constraints.lowerBound()) {
                bounded.add(name);
            } else {
                discrete.add(name);
            }

            if (model.hasSetter(name)) {
                modifiable.add(name);
            }

            fieldByName.put(fields[i].getName(), fields[i]);
        }

        this.bounded = Collections.unmodifiableList(bounded);
        this.discrete = Collections.unmodifiableList(discrete);
        this.modifiable = Collections.unmodifiableList(modifiable);
        this.columns = Collections.unmodifiableList(columns);
        this.byName = Collections.unmodifiableMap(fieldByName);
    }

    public List<Column> haveConstraint(Function<Constraints, Boolean> check) {
        return Stream
                .of(columns.toArray(Column[]::new))
                .filter(c -> check.apply(c.constraints))
                .toList();
    }

    public List<Column> ofType(Class<?> type) {
        return Stream
                .of(columns.toArray(Column[]::new))
                .filter(c -> c.type.equals(type))
                .toList();
    }

    // visible is relative to the user of the model. Sometimes the user only passes
    // strings that are then parsed to a LocalDate for example.
    public Class<?> visibleTypeOf(String name) {
        Class<?> type = byName.get(name).getType();
        if (type.equals(LocalDate.class)) {
            type = String.class;
        }
        return type;
    }
}
