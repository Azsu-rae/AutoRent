package gui.dashboard.record.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

import gui.component.*;
import gui.util.Attribute;
import gui.util.Parser;
import orm.Reflection;
import orm.Table.Range;

import static gui.util.Parser.titleCase;
import static gui.util.Parser.getMin;
import static gui.util.Parser.getMax;

public class RangeSelection extends MyDialog<Range> {

    JTextField[] fields = new JTextField[2];
    String[] labels = new String[2];

    Attribute attribute;
    public RangeSelection(String title, Attribute attribute, Function<Range,Boolean> callback) {
        super(title, callback);
        this.attribute = attribute;
    }

    @Override
    protected MyPanel initialize() {

        var constraints = Reflection.fieldsOf(attribute.model()).constraintsOf(attribute.name());
        if (constraints.lowerBound()) {
            labels[0] = titleCase(attribute.name()) + ":";
            labels[1] = titleCase(constraints.boundedPair()) + ":";
        } else if (constraints.bounded()) {
            labels[0] = getMin(attribute);
            labels[1] = getMax(attribute);
        }

        var fieldsPanel = Factory.createForm(labels, fields);

        var panel = new MyPanel();
        var gbc = Factory.initFormGBC();
        panel.setLayout(new GridBagLayout());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(fieldsPanel);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyButton("Save", e -> finalize("Invalid format!")), gbc);

        return panel;
    }

    @Override
    public Range parseInput() {
        var lowerVal = Parser.parse(new Attribute(attribute.model(), attribute.name()).setValue(fields[0].getText()));
        var upperVal = Parser.parse(new Attribute(attribute.model(), attribute.name()).setValue(fields[1].getText()));
        return new Range(attribute.name(), lowerVal, upperVal);

    }
}
