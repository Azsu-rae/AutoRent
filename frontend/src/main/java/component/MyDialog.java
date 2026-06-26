package component;

import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import gui.Opts;

abstract public class MyDialog<T> extends JDialog {

    Consumer<T> callback;

    public MyDialog(String title, Consumer<T> callback) {
        super(Opts.MAIN_FRAME, title, true);
        this.callback = callback;
    }

    public void display() {

        setContentPane(initialize());

        pack();
        Dimension d = getSize();
        setSize(Math.max(d.width, 200), Math.max(d.height, 200));

        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    public void submit(String errorMessageFormat, Object... values) {
        if (validateInput()) {
            callback.accept(parseInput());
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, String.format(errorMessageFormat, values));
        }
    }

    // The layout and everything you see
    abstract protected MyPanel initialize();

    // step 1: validate the input (non-empty fields, etc...)
    abstract protected boolean validateInput();

    // step 2: if valid, you parse it
    abstract protected T parseInput();
}
