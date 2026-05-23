package gui.component;

import java.util.function.Consumer;
import java.util.function.Function;

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
        var panel = initialize();
        // panel.setPreferredSize(new Dimension(200, 200));
        setContentPane(panel);
        pack();
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

    abstract protected boolean validateInput();

    abstract protected T parseInput();

    abstract protected MyPanel initialize();
}
