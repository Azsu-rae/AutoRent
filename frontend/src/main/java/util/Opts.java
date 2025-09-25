package util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Opts {

    public static boolean CUSTOM_THEME;
    public static JFrame MAIN_FRAME;
    static private List<ToClear> toClears = new ArrayList<>();

    public static void useCustomTheme(boolean useCustomTheme) {
        CUSTOM_THEME = useCustomTheme;
    }

    public static void setMainFrame(JFrame mainFrame) {
        MAIN_FRAME = mainFrame;
    }

    public static void addToClear(ToClear toClear) {
        toClears.add(toClear);
    }

    public static void clearEvent() {
        for (var toClear : toClears) {
            toClear.clear();
        }
    }
}
