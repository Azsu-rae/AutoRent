package orm.util;

public class Utils {

    public static void error(Exception e) {
        e.printStackTrace();
    }

    public static void error(Exception e, String[] suspects) {
        e.printStackTrace();
        for (String s : suspects) {
            error(s);
        }
    }

    public static void error(Exception e, String s, Object... args) {
        e.printStackTrace();
        error(s, args);
    }

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
