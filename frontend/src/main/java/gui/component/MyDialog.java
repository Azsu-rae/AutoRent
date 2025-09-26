package ui.component;

import javax.swing.JDialog;

import util.Opts;

public class MyDialog extends JDialog {
    public MyDialog(String title) {
        super(Opts.MAIN_FRAME, title, true);
    }
}
