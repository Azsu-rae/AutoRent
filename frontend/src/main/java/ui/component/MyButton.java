package ui.component;

import javax.swing.*;

import ui.style.*;
import util.Opts;

import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyButton extends JButton {

    public MyButton(String name, int alignment) {
        this(name);
        setHorizontalAlignment(alignment);
    }

    public MyButton(String name, int width, int height) {
        this(name);
        setPreferredSize(new Dimension(width, height));
    }

    public MyButton(String name) {

        super(name);
        if (Opts.DEFAULT_THEME) return;

        setBackground(MyColor.BUTTON);
        setFont(MyFont.BUTTON);
        setForeground(Color.WHITE);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

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

        if (Opts.DEFAULT_THEME) {
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
