package gui.dashboard.record.dialog;

import java.awt.*;

import javax.swing.JTextField;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import gui.component.*;
import gui.util.Attribute;
import gui.util.Parser;

public class SearchProfile extends MyDialog {

    Map<String,JTextField> fields = new HashMap<>();
    Function<Attribute[],Boolean> callback;
    String[] attributeNames;

    public SearchProfile(String[] attributeNames, Function<Attribute[],Boolean> callback) {
        super("Search Profile");
        this.callback = callback;
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
        panel.add(new MyButton("Save", e -> {
            finalize(callback.apply(parseInput()), "We good");
        }), gbc);

        return panel;
    }

    Attribute[] parseInput() {
        var attributes = new Attribute[fields.size()];
        for (int i=0;i<fields.size();i++) {
            attributes[i] = new Attribute(attributeNames[i], new String[] {
                fields.get(attributeNames[i]).getText()
            });
        } dispose();
        return attributes;
    }
}
