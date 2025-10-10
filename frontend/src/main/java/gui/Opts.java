package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import orm.util.BugDetectedException;

import gui.contract.ToClear;

public class Opts {

    public static boolean CUSTOM_THEME;
    public static JFrame MAIN_FRAME;
    static private List<ToClear> toClears = new ArrayList<>();

    public static void useCustomTheme(boolean useCustomTheme) {

        CUSTOM_THEME = useCustomTheme;
        if (useCustomTheme) {
            return;
        }

        // Theme
//        try {
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//            throw new BugDetectedException("Built-in theme has issues!");
//        }
    }

    public static void setMainFrame(JFrame mainFrame) {
        MAIN_FRAME = mainFrame;
    }

    public static void addToClear(ToClear toClear) {
        toClears.add(toClear);
    }

    public static void removeToClear(ToClear toClear) {
        toClears.remove(toClear);
    }

    public static void clearEvent() {
        for (var toClear : toClears) {
            toClear.clear();
        }
    }
}
