
import orm.Table;

import gui.MainFrame;

public class Main {

    public static void main(String[] args) {
        // must always run. DO NOT REMOVE
        Table.JVMInit(args[0].split("\n"));

        javax.swing.SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
