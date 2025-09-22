package util;

import javax.swing.JFrame;

public class Opts {

    public static boolean DEFAULT_THEME;
    public static JFrame MAIN_FRAME;

    public static void set(boolean useDefaultTheme, JFrame mainFrame) {
        MAIN_FRAME = mainFrame;
        DEFAULT_THEME = useDefaultTheme;
    }
}
