package gui.component;

import javax.swing.JDialog;

import gui.util.DialogManager;
import gui.util.Opts;

abstract public class MyDialog extends JDialog {

    public MyDialog(String title) {
        super(Opts.MAIN_FRAME, title, true);
        setContentPane(init());
        setSize(200, 200);
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    abstract protected MyPanel init();
    abstract protected void done(DialogManager manager);
}
