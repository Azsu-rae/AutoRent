package orm.util;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import java.util.stream.Stream;

import orm.Table;

public class Database {

    private static String path = "./BackendAPI/ressources/sample_data/";

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
                delete(Table.search(className));
            }
        }
    }

    public static void readSampleData() {

        for (String modelName : readOrderFile()) {

            File file = new File(path + modelName + ".txt");
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

        File order = new File(path + "order.txt");
        List<String> modelNames = new ArrayList<>();
        try (Scanner orderScanner = new Scanner(order)) {
            while (orderScanner.hasNext()) {
                String modelName = orderScanner.next();
                modelNames.add(modelName);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return modelNames;
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
