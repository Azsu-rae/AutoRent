package util;

/**
 * Log
 */
public class Log {

    public static void notice(String format, Object... args) {
        System.out.println();
        System.out.println(String.format(format, args));
    }

    public static void cinit(String format, Object... args) {
        System.out.println();
        System.out.println(String.format(format, args));
    }

    public static void sql(String format, Object... args) {
        System.out.println();
        System.out.println(String.format(format, args));
    }

    public static void fail(String format, Object... args) {
        System.out.println();
        System.out.println(String.format(format, args));
    }

    public static void error(String format, Object... args) {
        System.err.println();
        System.err.println(String.format(format, args));
    }
}
