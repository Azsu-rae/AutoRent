package gui.component;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MyButton extends JButton {

    public boolean defaultEnabled = true;

    public MyButton(Container container, String name, ActionListener l, boolean defaultEnable) {
        this(name, defaultEnable);
        addActionListener(l);
        container.add(this);
    }

    public MyButton(Container container, String name, ActionListener l) {
        super(name);
        addActionListener(l);
        container.add(this);
    }

    public MyButton(String name, int width, int height, ActionListener l) {
        this(name, width, height);
        addActionListener(l);
    }

    public MyButton(String name, ActionListener l, float alignment) {
        this(name, l);
        setAlignmentX(alignment);
    }

    public MyButton(String name, ActionListener l) {
        super(name);
        addActionListener(l);
    }

    public MyButton(String name, int width, int height) {
        super(name);
        setPreferredSize(new Dimension(width, height));
    }

    public MyButton(String name, boolean defaultEnabled) {
        super(name);
        setEnabled(defaultEnabled);
        this.defaultEnabled = defaultEnabled;
    }

    public MyButton(String name, int alignment) {
        super(name);
        setHorizontalAlignment(alignment);
    }
}
