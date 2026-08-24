package orm;

import util.*;
import static util.Log.*;

import java.lang.reflect.*;

public class Reflection {

    private Table<?> tuple;
    public Model<?> model;

    Reflection(Table<?> tuple) {
        this.tuple = tuple;
        this.model = tuple.model;
    }

    public FieldAction field(String fieldName) {
        return new FieldAction(model.fields.byName.get(fieldName));
    }

    private Object invoke(Method method, Object... args) {
        try {
            return method.invoke(tuple, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            error(e);
            throw new BugDetectedException("Bad Reflection Argument!");
        }
    }

    public class FieldAction {

        Field field;

        private FieldAction(Field field) {
            this.field = field;
        }

        public Object getValue() {
            try {
                return invoke(model.getGetter(field.getName()));
            } catch (NoSuchMethodException e) {
                throw new BugDetectedException("Attempting to read the value of an un-readable attribute!");
            }
        }

        public void setValue(Object value) {
            try {
                invoke(model.getSetter(field.getName()), value);
            } catch (NoSuchMethodException e) {
                throw new BugDetectedException("Attempting to modify the value of an un-modifiable attribute!");
            }
        }
    }
}
