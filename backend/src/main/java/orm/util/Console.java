package orm.util;

import java.util.Vector;

import orm.Table;
import orm.Table.Range;

public class Console {

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

    public static void print(Object o) {
        print("%s", o);
    }

    public static void print(String s, Object... args) {
        System.out.println(String.format("\n" + s, args));
    }

    public static void print(Vector<Range> ranges) {
        for (var range : ranges) {
            print(range.toString());
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

        print(s.toString());
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
}
