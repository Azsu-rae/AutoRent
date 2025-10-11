package gui.component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import gui.Opts;

abstract public class MyDialog extends JDialog {

    public MyDialog(String title) {
        super(Opts.MAIN_FRAME, title, true);
    }

    public void display() {
        setContentPane(initialize());
        pack();
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    public void finalize(boolean done, String errorMessageFormat, Object... values) {
        if (done) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, String.format(errorMessageFormat, values));
        }
    }

    abstract protected MyPanel initialize();
}
