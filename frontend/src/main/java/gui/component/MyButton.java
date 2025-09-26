package gui.component;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import gui.style.MyColor;
import gui.style.MyFont;
import gui.util.Opts;

public class MyButton extends JButton {

    public boolean defaultEnabled = true;

    public MyButton(Container container, String name, ActionListener l, boolean defaultEnable) {
        this(name, defaultEnable);
        addActionListener(l);
        container.add(this);
    }

    public MyButton(Container container, String name, ActionListener l) {
        this(name);
        addActionListener(l);
        container.add(this);
    }

    public MyButton(String name, ActionListener l, float alignment) {
        this(name, l);
        setAlignmentX(alignment);
    }

    public MyButton(String name, ActionListener l) {
        this(name);
        addActionListener(l);
    }

    public MyButton(String name, int width, int height) {
        this(name);
        setPreferredSize(new Dimension(width, height));
    }

    public MyButton(String name, boolean defaultEnabled) {
        this(name);
        setEnabled(defaultEnabled);
        this.defaultEnabled = defaultEnabled;
    }

    public MyButton(String name, int alignment) {
        this(name);
        setHorizontalAlignment(alignment);
    }

    public MyButton(String name) {

        super(name);
        setFocusPainted(false);
        if (!Opts.CUSTOM_THEME) return;

        setBackground(MyColor.BUTTON);
        setFont(MyFont.BUTTON);
        setForeground(Color.WHITE);

        setContentAreaFilled(false);
        setBorderPainted(false);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.CENTER);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(50, 80, 160));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(new Color(75, 110, 220));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        if (!Opts.CUSTOM_THEME) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        super.paintComponent(g);
        g2.dispose();
    }
}
