package gui.dashboard.record.dialog;

import java.awt.*;

import javax.swing.JTextField;

import java.util.*;
import java.util.List;
import java.util.function.Function;

import gui.util.Attribute;
import gui.util.Parser;
import gui.component.*;

public class SearchProfile extends MyDialog<List<Attribute<String>>> {

    Map<String,JTextField> fields = new HashMap<>();
    String[] attributeNames;

    public SearchProfile(String[] attributeNames, Function<List<Attribute<String>>,Boolean> callback) {
        super("Search Profile", callback);
        this.attributeNames = attributeNames;
    }

    @Override
    protected MyPanel initialize() {

        var placeholders = new JTextField[attributeNames.length];
        var labels = Parser.titleCaseNames(attributeNames);
        var form = Factory.createForm(labels, placeholders);

        int i=0;
        for (var att : attributeNames) {
            fields.put(att, placeholders[i++]);
        }

        var gbc = Factory.initFormGBC();
        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(form);

        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyButton("Search", e -> finalize("This shouldn't happen")), gbc);

        return panel;
    }

    @Override
    protected List<Attribute<String>> parseInput() {
        return fields
            .keySet()
            .stream()
            .map(field -> new Attribute<String>(field).addValue(fields.get(field).getText()))
            .toList();
    }
}
