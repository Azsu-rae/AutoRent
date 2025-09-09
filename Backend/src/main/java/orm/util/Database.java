package orm.util;

import static orm.util.Utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import orm.Table;

public class Database {

    private static String path = "./Backend/ressources/samples/";

    private static HashMap<Pair<String,String>,Integer> occurences = new HashMap<>();

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

                System.out.println(String.format(s, className));
            }
        }
    }

    public static void readSampleData() {

        for (String modelName : readOrderFile()) {
            JSONArray tuples = new JSONArray(readJson(modelName));
            try (Scanner scanner = new Scanner(file)) {

                Vector<String> lines = new Vector<>();
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
                input(parse(lines, modelName));

            } catch (FileNotFoundException e) {
                throw new IllegalStateException("File not found: " + e.getMessage());
            }
        }
    }

    private static List<String> readOrderFile() {
        return Arrays.asList(((String) new JSONArray(readJson("order")).getJSONObject(0).get("order"))
            .split(" ")
        );
    }

    private static String readJson(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(path + fileName + ".json")));
        } catch (IOException e) {
            error(e);
            return null;
        }
    }

    public static void print(Vector<? extends Table> tuples, String name) {

        StringBuilder s = new StringBuilder();

        s.append("\n");
        if (tuples == null) {
            s.append("No tuples passed (null!) called " + name + "!");
        } else if (tuples.size() == 0) {
            s.append("No Results for " + name + "!");
        } else {
            s.append(" --------------");
            s.append(" Search Result for " + name);
            s.append(" ------------");
            s.append("\n\n");
        }
        s.append(toString(tuples));

        System.out.println(s.toString());
    }

    private static Vector<Table> parse(Vector<String> lines, String className) {

        Vector<Table> tuples = new Vector<>();
        for (String line : lines) {

            List<Object> args = Stream.of(line.split(" ")).map(arg -> {
                if (Table.hasSubClass(arg)) {
                    return getSample(className, arg);
                } else {
                    return arg;
                }
            }).toList();

            Table tuple = Reflection.getModelInstance(className, args.toArray());
            tuples.add(tuple);
        }

        return tuples;
    }

    private static Table getSample(String model, String attribute) {

        Pair<String,String> key = new Pair<>(model,attribute);
        int index = occurences.computeIfAbsent(key, k -> 0);
        occurences.put(key, index+1);

        return Table.search(attribute).elementAt(index);
    }

    public static String toString(Vector<? extends Table> tuples) {

        StringBuilder s = new StringBuilder();
        boolean first = true;

        for (Table tuple : tuples) {
            s.append((first ? "" : "\n\n") + tuple);
            first = false;
        }

        return s.toString();
    }

    public static boolean input(Vector<? extends Table> tuples) {

        boolean success = true;
        for (Table tuple : tuples) {
            success = success && tuple.add();
        }

        return success;
    }

    public static boolean delete(Vector<? extends Table> tuples) {

        boolean success = true;
        for (Table tuple : tuples) {
            success = success && tuple.delete();
        }

        return success;
    }
}
