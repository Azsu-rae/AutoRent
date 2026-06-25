package gui.dashboard.record.dialog;

import javax.swing.*;
import java.awt.*;

import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;

import component.*;

import mapper.FieldLabelFormatter.RangeLabel;
import mapper.FieldValueMapper.RangeParser;

import orm.Table.Range;

public class RangeSelection extends MyDialog<Range> {

    Map<String, JTextField> fields = new HashMap<>();
    String[] labels = new String[2];

    RangeParser parser;
    RangeLabel label;

    public RangeSelection(String title, RangeLabel label, RangeParser parser, Consumer<Range> callback) {
        super(title, callback);
        this.label = label;
        this.parser = parser;
    }

    @Override
    protected MyPanel initialize() {

        labels[0] = label.start();
        labels[1] = label.end();

        var fieldsPanel = Factory.createForm(labels, fields);

        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(fieldsPanel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyButton("Save", e -> submit("Invalid format!")), gbc);

        return panel;
    }

    @Override
    protected boolean validateInput() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected Range parseInput() {
        return parser.parse(fields.get(labels[0]).getText(), fields.get(labels[1]).getText());
    }
}
