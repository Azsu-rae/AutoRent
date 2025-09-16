package ui;

import javax.swing.*;
import java.awt.*;

import ui.style.RoundedBorder;

public class Factory {

    public enum Field {
        TEXT, PASSWORD
    }

    static public JTextField field(int columns, Field type) {

        JTextField field = null;
        switch (type) {
            case TEXT:
                field = new JTextField(columns);
                break;
            case PASSWORD:
                field = new JPasswordField(columns);
            default:
                break;
        }

        field.setBackground(new Color(69, 73, 74)); // Dark gray background
        field.setForeground(Color.WHITE); // White text
        field.setBorder(new RoundedBorder(15));
        field.setOpaque(false);
//        field.setBorder(BorderFactory.createLineBorder(new Color(87, 90, 92))); // Subtle border

        return field;
    }
}
