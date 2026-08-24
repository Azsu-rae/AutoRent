
import orm.Reflection;
import orm.Table;

import gui.MainFrame;
import model.TeachingAssistant;

public class Main {

    public static void main(String[] args) {
        // must always run. DO NOT REMOVE
        Reflection.JVMInit(args[0].split("\n"));
        javax.swing.SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
