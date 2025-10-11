package gui.dashboard.record.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import gui.component.*;
import gui.util.Attribute;
import gui.Opts;
import orm.Reflection;
import orm.Table.Range;
import orm.util.Console;

import static gui.util.Parser.titleCase;
import static gui.util.Parser.getMin;
import static gui.util.Parser.getMax;

public class RangeSelection extends MyDialog {

    JTextField[] fields = new JTextField[2];
    String[] labels = new String[2];

    Attribute attribute;
    BiFunction<String,String,Boolean> callback;

    public RangeSelection(String title, Attribute attribute, BiFunction<String,String,Boolean> callback) {
        super(title);
        this.attribute = attribute;
        this.callback = callback;
    }

    @Override
    protected MyPanel initialize() {
        var constraints = Reflection.fieldsOf(attribute.model()).constraintsOf(attribute.name());
        if (constraints.lowerBound()) {
            labels[0] = titleCase(attribute.name());
            labels[1] = titleCase(constraints.boundedPair());
        } else if (constraints.bounded()) {
            labels[0] = getMin(attribute);
            labels[1] = getMax(attribute);
        }

        var fieldsPanel = Factory.createForm(labels, fields);

        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());
        var gbc = Factory.initFormGBC();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(fieldsPanel);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new MyButton("Save", e -> {
            finalize(callback.apply(fields[0].getText(), fields[1].getText()), "Invalid format!");
        }), gbc);

        return panel;
    }
}
