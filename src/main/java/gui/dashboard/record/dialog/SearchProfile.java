package gui.dashboard.record.dialog;

import java.awt.*;

import javax.swing.JTextField;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import util.Parser;
import component.*;

public class SearchProfile extends MyDialog<Map<String, String>> {

    Map<String, JTextField> fields = new HashMap<>();
    String[] attributeNames;

    public SearchProfile(String[] attributeNames, Consumer<Map<String, String>> callback) {
        super("Search Profile", callback);
        this.attributeNames = attributeNames;
    }

    @Override
    protected MyPanel initialize() {

        var form = Factory.createForm(attributeNames, fields);
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(form, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyButton("Search", e -> submit("This shouldn't happen")), gbc);

        return panel;
    }

    @Override
    protected boolean validateInput() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Map<String, String> parseInput() {
        var attributes = new HashMap<String, String>();
        fields
                .keySet()
                .stream()
                .forEach(field -> {
                    var text = fields.get(field).getText();
                    if (!text.equals(""))
                        attributes.put(field, text);
                });
        return attributes;
    }
}
