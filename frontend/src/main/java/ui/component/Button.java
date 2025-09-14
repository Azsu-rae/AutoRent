package ui.component;

import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ui.style.RoundedBorder;

public class Button extends JButton {

    public Button(String name) {

        super(name);
//        setBackground(new Color(75, 110, 175)); // Blue background
//        setFocusPainted(false);     // no ugly focus border
//        setMargin(new Insets(10, 20, 10, 10)); // padding (top, left, bottom, right)
//        setBorderPainted(false);    // no border
//        setForeground(Color.WHITE);
//        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // hand on hover
//        setFont(new Font("Segoe UI", Font.BOLD, 16));
//        setHorizontalAlignment(SwingConstants.LEFT); // text aligned left

        // Hover effect
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                setBackground(new Color(95, 130, 200)); // Lighter blue on hover
//            }
//            @Override
//            public void mouseExited(MouseEvent e) {
//                setBackground(new Color(75, 110, 175)); // Original blue
//            }
//        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // fill background with rounded shape
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.GRAY);
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
    }
}
