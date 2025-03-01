package dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Styling {
    public static void styleTextLabel(JLabel textLabel) {
        textLabel.setBackground(new Color(69, 73, 74)); // Dark gray background
        textLabel.setForeground(Color.WHITE); // White text
        textLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Font size
    }

    public static void styleTextField(JTextField textField) {
        textField.setBackground(new Color(69, 73, 74)); // Dark gray background
        textField.setForeground(Color.WHITE); // White text
        textField.setCaretColor(Color.WHITE); // White cursor
        textField.setBorder(BorderFactory.createLineBorder(new Color(87, 90, 92))); // Subtle border
        textField.setFont(new Font("Arial", Font.PLAIN, 18)); // Font size
    }

    public static void styleButton(JButton button) {
        button.setBackground(new Color(75, 110, 175)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Font size
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding inside button
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

        // Rounded corners
        button.setBorder(BorderFactory.createLineBorder(new Color(87, 90, 92), 2, true));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(95, 130, 200)); // Lighter blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(75, 110, 175)); // Original blue
            }
        });
    }
}
