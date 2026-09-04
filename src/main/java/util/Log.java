package util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import orm.Table;

/**
 * Log
 */
public class Log {

    public static void notice(String format, Object... args) {
        print(String.format(format, args));
    }

    public static void cinit(String format, Object... args) {
        print(String.format(format, args));
    }

    public static void sql(String format, Object... args) {
        print(String.format(format, args));
    }

    public static void debug(String id, Object obj) {
        print(id + obj);
    }

    public static void fail(String format, Object... args) {
        print(String.format(format, args));
    }

    public static void error(String format, Object... args) {
        print(String.format(format, args));
    }

    public static void error(Exception e) {
        print(e);
    }

    public static void printArray(Object[] objects) {
        print(arrayToString(objects));
    }

    public static void print(String template, Object... args) {
        IO.println("\n" + String.format(template, args));
    }

    public static void print(Object obj) {
        IO.println("\n" + obj);
    }

    public static String arrayToString(Object... objects) {
        return "[" + Stream.of(objects).map(o -> o.toString()).collect(Collectors.joining(", ")) + "]";
    }

    public static String toString(List<? extends Table<?>> list) {
        return list.stream().map(s -> s.toString()).collect(Collectors.joining("\n\n"));
    }
}
