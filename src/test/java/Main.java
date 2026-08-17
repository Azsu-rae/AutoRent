
import orm.Table;

import gui.MainFrame;
import model.TeachingAssistant;

public class Main {

    public static void main(String[] args) {
        // must always run. DO NOT REMOVE
        Table.JVMInit(args[0].split("\n"));

        TeachingAssistant.isSearchable();
        javax.swing.SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
