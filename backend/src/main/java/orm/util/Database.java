package orm.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.JSONArray;

import orm.Table;

import static orm.util.Reflection.getModelInstance;
import static orm.util.Console.*;

public class Database {

    private static Map<Aggregation,Integer> occurences = new HashMap<>();
    private static String path = "./Backend/ressources/samples/";

    public static void readSampleData() {
        for (String modelName : readOrderFile()) {
            read(modelName);
        }
    }

    public static void display() {
        for (String className : Table.getModelNames()) {
            if (Table.isSearchable(className)) {
                print(Table.search(className), className);
            }
        }
    }

    public static void clear() {
        for (String className : Table.getModelNames()) {
            if (Table.isSearchable(className)) {
                String s;
                if (delete(Table.search(className))) {
                    s = "Deleted: %s";
                } else {
                    s = "Deletion failed for: %s";
                }
                print(s, className);
            }
        }
    }

    public static boolean input(Vector<? extends Table> tuples) {
        boolean success = true;
        for (Table tuple : tuples) {
            success = success && tuple.add() >= 1;
        } return success;
    }

    public static boolean delete(Vector<? extends Table> tuples) {
        boolean success = true;
        for (Table tuple : tuples) {
            success = success && tuple.delete() >= 1;
        } return success;
    }

    private static Table getSample(String ofThisModel, String forThisModel) {

        var key = new Aggregation(ofThisModel, forThisModel);
        int index = occurences.computeIfAbsent(key, k -> 0);
        occurences.put(key, index+1);

        return Table.search(ofThisModel).elementAt(index);
    }

    private static void read(String model) {

        var parsed = new Vector<Table>();
        var tuples = new JSONArray(readJson(model));

        for (int i=0;i<tuples.length();i++) {

            var tuple = tuples.getJSONObject(i);
            var instance = getModelInstance(model);

            for (String field : instance.reflect.fields.names) {
                if (tuple.has(field)) {
                    Object value = tuple.get(field);
                    if (Table.getModelNames().contains(value)) {
                        value = getSample((String)value, model);
                    } else if (value instanceof java.math.BigDecimal) {
                        value = ((java.math.BigDecimal) value).doubleValue();
                    } instance.reflect.fields.callSetter(field, value);
                }
            } parsed.add(instance);
        }

        input(parsed);
    }

    private static List<String> readOrderFile() {
        return Arrays.asList(((String) new JSONArray(readJson("order")).get(0)).split(" "));
    }

    private static String readJson(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(path + fileName + ".json")));
        } catch (IOException e) {
            error(e);
            return null;
        }
    }

    private static class Aggregation extends Pair<String,String> {
        Aggregation(String composite, String component) {
            super(composite, component);
        }
    }
}
