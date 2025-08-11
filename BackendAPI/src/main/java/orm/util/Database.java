package orm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Stream;

import orm.Table;

public class Database {

    private static String path = "./ressources/data/";

    private static HashMap<String,Integer> occurences = new HashMap<>();

    static {
        Reflection.init();
    }

    public static void display() {

        for (String className : Table.getModelNames()) {
            Table tuple = Reflection.getModelInstance(className);
            if (Table.isSearchable(tuple)) {
                print(Table.search(tuple), className);
            }
        }
    }

    public static void clear() {

        for (String className : Table.getModelNames()) {
            Table tuple = Reflection.getModelInstance(className);
            if (Table.isSearchable(tuple)) {
                delete(Table.search(tuple));
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
                modelNames.add(orderScanner.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return modelNames;
    }

    public static void print(Vector<? extends Table> tuples, String name) {

        StringBuilder s = new StringBuilder();

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
                if (Table.getModelNames().contains(arg)) {
                    return getSample(arg);
                } else {
                    return arg;
                }
            }).toList();
            tuples.add(Reflection.getModelInstance(className, args.toArray()));
        }

        return tuples;
    }

    private static Table getSample(String model) {

        int index = occurences.computeIfAbsent(model, k -> 1);
        occurences.put(model, index+1);

        return Table.search(model).elementAt(index);
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
            success = success && Table.add(tuple);
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
