package utilities;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.Vector;

import orm.Table;

public class UtilityMethods {

    public static String tuplesToString(Vector<? extends Table> tuples, String s) {

        StringBuilder result = new StringBuilder();
        if (tuples.size() == 0) {
            result.append("\nNo Results for " + s + "!");
        } else {
            result.append(
                "\n" +
                " --------------" + 
                " Search Result for " + s + 
                " --------------"
            );
            for (Table tuple : tuples) {
                result.append("\n\n" + tuple);
            }
        }

        return result.toString();
    }

    public static void print(Vector<? extends Table> tuples, String s) {

        System.out.println(tuplesToString(tuples, s));
    }

    public static void printDB() {

        for (String className : Table.info.subClasses) {
            print(Table.search(ReflectionUtils.getModelInstance(className)), className);
        }
    }

    public static void deleteDB() {

        for (String className : Table.info.subClasses) {
            deleteTuples(Table.search(ReflectionUtils.getModelInstance(className)));
        }
    }

    public static void readData() {

        String[] classNames = Table.info.subClasses;
        String path = "./ressources/data/";
        for (int i=0;i<classNames.length;i++) {
            File file = new File(path + classNames[i] + ".txt");
            try (Scanner scanner = new Scanner(file)) {
                Vector<String> lines = new Vector<>();
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
                inputTuples(lines, classNames[i]);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("File not found: " + e.getMessage());
            }
        }
    }

    private static void inputTuples(Vector<String> lines, String className) {

        for (String line : lines) {
            Object[] args = line.split(" ");
            // System.out.println("Inserting: " + Arrays.toString(args));
            Table tuple = ReflectionUtils.getModelInstance(className, args);
            Table.add(tuple);
        }
    }

    public static void deleteTuples(Vector<Table> tuples) {

        for (Table tuple : tuples) {
            tuple.delete();
        }
    }
}
