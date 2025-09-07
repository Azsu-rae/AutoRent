package orm.util;

public class Utils {

    public static void error(String s, Object... args) {
        System.err.println(String.format("\n" + s, args));
    }

    public static void print(String s, Object... args) {
        System.out.println(String.format("\n" + s, args));
    }

    public static String format(String s, Object... args) {
        return String.format("\n" + s, args);
    }
}
