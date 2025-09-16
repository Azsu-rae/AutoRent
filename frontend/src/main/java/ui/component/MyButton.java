package ui.component;

import javax.swing.*;

import ui.style.MyStyle;

import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyButton extends JButton {

    public MyButton(String name) {

        super(name);

        setBackground(MyStyle.BUTTON);
        setFont(MyStyle.BUTTON_FONT);
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

    public MyButton(String name, int alignment) {
        this(name);
        setHorizontalAlignment(alignment);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        super.paintComponent(g);
        g2.dispose();
    }
}
