package gui.component;

import java.util.function.Function;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import gui.Opts;

abstract public class MyDialog<T> extends JDialog {

    Function<T,Boolean> callback;
    public MyDialog(String title, Function<T,Boolean> callback) {
        super(Opts.MAIN_FRAME, title, true);
        this.callback = callback;
    }

    public void display() {
        setContentPane(initialize());
        pack();
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    public void finalize(String errorMessageFormat, Object... values) {
        if (callback.apply(parseInput())) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, String.format(errorMessageFormat, values));
        }
    }

    abstract protected T parseInput();

    abstract protected MyPanel initialize();
}
