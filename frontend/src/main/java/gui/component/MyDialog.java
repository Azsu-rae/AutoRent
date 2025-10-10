package gui.component;

import javax.swing.JDialog;

import gui.Opts;

abstract public class MyDialog extends JDialog {

    public MyDialog(String title) {
        super(Opts.MAIN_FRAME, title, true);
    }

    public void showDialog() {
        setContentPane(initDialog());
        pack();
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    abstract protected MyPanel initDialog();
}
