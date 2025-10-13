package gui.component;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Factory {

    static public MyPanel createSearchBar(Consumer<String> callback) {

        var panel = new MyPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        panel.add(new MyLabel("Search"));

        var searchField = Factory.createField(20, Field.TEXT);
        panel.add(searchField);
        panel.add(new MyButton("Search", e -> callback.accept(searchField.getText())));

        panel.setOpaque(false);
        panel.setMaximumSize(panel.getPreferredSize());

        return panel;
    }

    static public GridBagConstraints initFormGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    static public MyPanel createForm(String[] label, JTextField[] placeholder) {

        if (label.length != placeholder.length) {
            throw new IllegalArgumentException("Different number of Labels and Fields!");
        }

        var gbc = initFormGBC();
        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());

        for (int i=0;i<label.length;i++) {

            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new MyLabel(label[i]), gbc);

            placeholder[i] = createField(20, Field.TEXT);
            gbc.gridx = 1; gbc.gridy = i; gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(placeholder[i], gbc);
        }

        return panel;
    }

    // Text Fields

    public enum Field {
        TEXT, PASSWORD
    }

    static public JTextField createField(MyPanel panel, String label) {
        var field = createField(Field.TEXT);
        panel.add(new MyLabel(label));
        panel.add(field);
        return field;
    }

    static public JTextField createField(int columns, Field type, int height, int width) {
        JTextField field = createField(columns, type);
        field.setPreferredSize(new Dimension(300, 40));
        return field;
    }

    static public JTextField createField(int columns, Field type) {
        JTextField field = createField(type);
        field.setColumns(columns);
        return field;
    }

    static public JTextField createField(Field type) {

        JTextField field = null;
        switch (type) {
            case TEXT:
                field = new JTextField();
                break;
            case PASSWORD:
                field = new JPasswordField();
            default:
                break;
        } return field;
    }
}
