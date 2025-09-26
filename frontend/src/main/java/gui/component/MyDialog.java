package gui.component;

import javax.swing.JDialog;

import gui.util.Opts;

public class MyDialog extends JDialog {
    public MyDialog(String title) {
        super(Opts.MAIN_FRAME, title, true);
    }
}
