package ui.component;

import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyButton extends JButton {

    public MyButton(String name) {

        super(name);

        setBackground(new Color(75, 110, 220)); // Blue background
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 16));

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);     // no ugly focus border

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  // hand on hover
        setHorizontalAlignment(SwingConstants.LEFT);                // text aligned left
        setMargin(new Insets(5, 20, 5, 10));                        // padding (top, left, bottom, right)

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(50, 80, 160)); // Lighter blue on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(new Color(75, 110, 220)); // Original blue
            }
        });
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
