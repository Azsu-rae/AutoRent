package component;

import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton {

    public Button(String name) {

        super(name);
        setBackground(new Color(75, 110, 175)); // Blue background
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(95, 130, 200)); // Lighter blue on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(new Color(75, 110, 175)); // Original blue
            }
        });
    }
}
