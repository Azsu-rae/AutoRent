package ui;

import javax.swing.*;
import java.awt.*;

import ui.component.MyLabel;
import ui.component.MyPanel;
import ui.style.MyBorder;
import util.Opts;

public class Factory {

    public enum Field {
        TEXT, PASSWORD
    }

    static public JTextField field(MyPanel panel, String label) {
        var field = Factory.field(Field.TEXT);
        panel.add(new MyLabel(label));
        panel.add(field);
        return field;
    }

    static public JTextField field(int columns, Field type, int height, int width) {
        JTextField field = field(columns, type);
        field.setPreferredSize(new Dimension(300, 40));
        return field;
    }

    static public JTextField field(int columns, Field type) {
        JTextField field = field(type);
        field.setColumns(columns);
        return field;
    }

    static public JTextField field(Field type) {

        JTextField field = null;
        switch (type) {
            case TEXT:
                field = new JTextField();
                break;
            case PASSWORD:
                field = new JPasswordField();
            default:
                break;
        }
        if (!Opts.CUSTOM_THEME) return field;

        field.setBackground(new Color(69, 73, 74)); // Dark gray background
        field.setForeground(Color.WHITE); // White text
        field.setBorder(new MyBorder.Rounded(15));
        field.setOpaque(false);
//        field.setBorder(BorderFactory.createLineBorder(new Color(87, 90, 92))); // Subtle border

        return field;
    }
}
